package pl.manyroutes.service;

import pl.manyroutes.controller.dto.UserRequestDto;
import pl.manyroutes.entity.User;
import pl.manyroutes.entity.UserChallenge;
import pl.manyroutes.entity.enums.Role;
import pl.manyroutes.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;


    @BeforeEach
    void setup() {
        userService = new UserService(userRepository);
    }


    @Test
    void getUsers_shouldReturnAllUsersButNotDeleted() {
        //given:
        User user1 = Instancio.create(User.class);
        User user2 = Instancio.create(User.class);
        User user3 = Instancio.of(User.class)
                .set(field(User::getEmail), "deleted-xxx")
                .create();
        List<User> users = List.of(user1, user2, user3);
        Mockito.when(userRepository.findAllWithChallenges()).thenReturn(users);

        //when:
        List<User> actual = userService.getUsers();

        //then:
        Assertions.assertThat(actual).containsExactly(user1, user2)
                .doesNotContain(user3);
    }

    @Test
    void getUsers_shouldReturnEmptyList() {
        //given:
        User user3 = Instancio.of(User.class)
                .set(field(User::getEmail), "deleted-xxx")
                .create();
        List<User> users = List.of(user3);
        Mockito.when(userRepository.findAllWithChallenges()).thenReturn(users);

        //when:
        List<User> actual = userService.getUsers();

        //then:
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    void getUser_shouldReturnUser() {
        //given:
        UUID id = UUID.randomUUID();
        User user = Instancio.of(User.class)
                .set(field(User::getId), id)
                .create();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        //when:
        User actual = userService.getUser(id);

        //then:
        Assertions.assertThat(actual).isEqualTo(user);
    }

    @Test
    void getUser_shouldReturn404() {
        //given:
        UUID id = UUID.randomUUID();

        //when and then:
        getUserById_shouldReturn404(() -> userService.getUser(id));
    }

    @Test
    void updateUser_shouldUpdateUserAndReturnUpdatedUser() {
        //given:
        User user = Instancio.of(User.class)
                .set(field(User::getName), "Adam")
                .create();
        UserRequestDto userRequestDto = new UserRequestDto(
                "bogdan@email.com",
                "Bogdan",
                "123456789",
                true,
                1.11,
                2.22);

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        //when:
        User actual = userService.updateUser(user.getId(), userRequestDto);

        //then:
        Assertions.assertThat(actual.getEmail()).isEqualTo(userRequestDto.email());
        Assertions.assertThat(actual.getName()).isEqualTo(userRequestDto.name());
        Assertions.assertThat(actual.getPhone()).isEqualTo(userRequestDto.phone());
        Assertions.assertThat(actual.getNewsletter()).isEqualTo(userRequestDto.newsletter());
        Assertions.assertThat(actual.getCoordinatesArray()).containsExactly(userRequestDto.latitude(),
                userRequestDto.longitude());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userCaptor.capture());
        Assertions.assertThat(userCaptor.getValue().getEmail()).isEqualTo(userRequestDto.email());
        Assertions.assertThat(userCaptor.getValue().getName()).isEqualTo(userRequestDto.name());
        Assertions.assertThat(userCaptor.getValue().getPhone()).isEqualTo(userRequestDto.phone());
        Assertions.assertThat(userCaptor.getValue().getNewsletter()).isEqualTo(userRequestDto.newsletter());
        Assertions.assertThat(userCaptor.getValue().getCoordinatesArray()).containsExactly(userRequestDto.latitude(),
                userRequestDto.longitude());
    }

    @Test
    void updateUser_shouldReturn404() {
        //given:
        UUID id = UUID.randomUUID();
        UserRequestDto dto = Instancio.create(UserRequestDto.class);

        //when and then:
        getUserById_shouldReturn404(() -> userService.updateUser(id, dto));
    }

    @Test
    void getUserScore_shouldReturnScore() {
        //given:
        UUID id = UUID.randomUUID();
        User user = Instancio.of(User.class)
                .set(field(User::getId), id)
                .setBlank(field(User::getUserChallengesSet))
                .create();
        user.setUserChallengesSet(Set.of(
                Instancio.of(UserChallenge.class)
                        .set(field(UserChallenge::getScore), 10)
                        .create(),
                Instancio.of(UserChallenge.class)
                        .set(field(UserChallenge::getScore), 20)
                        .create()
        ));
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        //when:
        int actual = userService.getUserScore(id);

        //then:
        Assertions.assertThat(actual).isEqualTo(30);
    }

    @Test
    void getUserScore_shouldReturnZero() {
        //given:
        UUID id = UUID.randomUUID();
        User user = Instancio.of(User.class)
                .set(field(User::getId), id)
                .setBlank(field(User::getUserChallengesSet))
                .create();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        //when:
        int actual = userService.getUserScore(id);

        //then:
        Assertions.assertThat(actual).isEqualTo(0);
    }

    @Test
    void getUserScore_shouldReturn404() {
        //given:
        UUID id = UUID.randomUUID();

        //when and then:
        getUserById_shouldReturn404(() -> userService.getUserScore(id));
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        //given:
        User user = Instancio.create(User.class);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        //when:
        userService.deleteUser(user.getId());

        //then:
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).delete(userCaptor.capture());
        Assertions.assertThat(userCaptor.getValue()).isEqualTo(user);
    }

    @Test
    void deleteUser_shouldReturn404() {
        //given:
        UUID id = UUID.randomUUID();

        //when and then:
        getUserById_shouldReturn404(() -> userService.deleteUser(id));
    }

    @Test
    void softDeleteUser_shouldDeleteUser() {
        //given:
        User user = new User("login", "password", "email@email.com", Role.USER);
        user.setPhone("123456789");
        user.setName("Adam");
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        //when:
        userService.softDeleteUser(user.getId());

        //then:
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userCaptor.capture());
        Assertions.assertThat(userCaptor.getValue().getName()).isEqualTo("****");
        Assertions.assertThat(userCaptor.getValue().getLogin()).isEqualTo("*****");
        Assertions.assertThat(userCaptor.getValue().getHashPassword()).isEqualTo("********");
        Assertions.assertThat(userCaptor.getValue().getPhone()).isEqualTo("*********");
        Assertions.assertThat(userCaptor.getValue().getEmail()).startsWith("deleted-");
        Assertions.assertThat(userCaptor.getValue().getNewsletter()).isEqualTo(false);
        Assertions.assertThat(userCaptor.getValue().getDeletedAt()).isNotNull();
    }

    @Test
    void softDeleteUser_shouldDeleteUser_whenUserContainsNullFields() {
        //given:
        User user = new User(null, null, null, null);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        //when:
        userService.softDeleteUser(user.getId());

        //then:
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userCaptor.capture());
        Assertions.assertThat(userCaptor.getValue().getName()).isNull();
        Assertions.assertThat(userCaptor.getValue().getLogin()).isNull();
        Assertions.assertThat(userCaptor.getValue().getHashPassword()).isNull();
        Assertions.assertThat(userCaptor.getValue().getPhone()).isNull();
        Assertions.assertThat(userCaptor.getValue().getEmail()).isNull();
        Assertions.assertThat(userCaptor.getValue().getNewsletter()).isEqualTo(false);
        Assertions.assertThat(userCaptor.getValue().getDeletedAt()).isNotNull();
    }

    @Test
    void softDeleteUser_shouldReturn404() {
        //given:
        UUID id = UUID.randomUUID();

        //when and then:
        getUserById_shouldReturn404(() -> userService.softDeleteUser(id));
    }

    private void getUserById_shouldReturn404(ThrowableAssert.ThrowingCallable throwingCallable) {
        //given:
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        //when:
        Throwable actual = Assertions.catchThrowable(
                throwingCallable
        );

        //then:
        Assertions.assertThat(actual)
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }
}
