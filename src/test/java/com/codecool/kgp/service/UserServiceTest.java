package com.codecool.kgp.service;

import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.UserRequestDto;
import com.codecool.kgp.errorhandling.DuplicateEntryException;
import com.codecool.kgp.mappers.UserMapper;
import com.codecool.kgp.repository.User;
import com.codecool.kgp.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private final UserRepository userRepository = Mockito.mock();
    private final UserMapper userMapper = Mockito.mock();
    private final UserService userService = new UserService(userRepository, userMapper);

    @Test
    void getUsers_shouldReturnAllUsersButNotDeleted() {
        //given:
        User user1 = Instancio.create(User.class);
        User user2 = Instancio.create(User.class);
        User user3 = Instancio.of(User.class)
                .generate(field(User::getEmail), gen -> gen.oneOf("deleted-xxx"))
                .create();
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3));

        UserDto user1Dto = Instancio.create(UserDto.class);
        UserDto user2Dto = Instancio.create(UserDto.class);

        Mockito.when(userMapper.mapEntityToDto(user1)).thenReturn(user1Dto);
        Mockito.when(userMapper.mapEntityToDto(user2)).thenReturn(user2Dto);

        //when:
        List<UserDto> actual = userService.getUsers();

        //then:
        Assertions.assertThat(actual).isEqualTo(List.of(user1Dto, user2Dto));
    }

    @Test
    void getUserByLogin_shouldReturnUserWithLogin() {
        //given:
        String login = "abc";
        User user = Instancio.of(User.class)
                .set(field(User::getLogin), login)
                .create();
        UserDto userDto = Instancio.of(UserDto.class)
                .set(field(UserDto::login), login)
                .create();
        Mockito.when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.mapEntityToDto(user)).thenReturn(userDto);

        //when:
        UserDto actual = userService.getUserByLogin(login);

        //then:
        Assertions.assertThat(actual.login()).isEqualTo(login);
    }

    @Test
    void getUserByLogin_shouldReturnResponseStatusException() {
        //given:
        String login = "abc";
        Mockito.when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        //when:
        Throwable actual = Assertions.catchThrowable(
                () -> userService.getUserByLogin(login)
        );

        //then:
        Assertions.assertThat(actual)
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    void setUser_shouldAddUserToRepositoryAndReturnUserDto() {
        //given:
        UserRequestDto userRequestDto = Instancio.create(UserRequestDto.class);
        User user = Instancio.create(User.class);
        User userFromDb = Instancio.create(User.class);
        UserDto userDto = Instancio.create(UserDto.class);

        Mockito.when(userMapper.mapRequestDtoToEntity(userRequestDto)).thenReturn(user);
        Mockito.when(userRepository.saveAndFlush(user)).thenReturn(userFromDb);
        Mockito.when(userMapper.mapEntityToDto(userFromDb)).thenReturn(userDto);

        //when:
        UserDto actual = userService.setUser(userRequestDto);

        //then:
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(userDto);
    }

    @Test
    void setUser_shouldThrowDuplicateEntryException() {
        //given:
        UserRequestDto userRequestDto = Instancio.create(UserRequestDto.class);
        User user = Instancio.create(User.class);

        Mockito.when(userMapper.mapRequestDtoToEntity(userRequestDto)).thenReturn(user);
        Mockito.when(userRepository.saveAndFlush(user)).thenThrow(DataIntegrityViolationException.class);

        //when and then:
        Assertions.assertThatThrownBy(() -> userService.setUser(userRequestDto))
                .isInstanceOf(DuplicateEntryException.class);
    }

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void updateUser_shouldUpdateUserAndReturnUserDto() {
        //given:
        User user = Instancio.of(User.class)
                .set(field(User::getName), "Adam")
                .create();
        UserRequestDto userRequestDto = Instancio.of(UserRequestDto.class)
                .set(field(UserRequestDto::name), "Bogdan")
                .create();
        UserDto userDto = Instancio.of(UserDto.class)
                .set(field(UserDto::name), "Bogdan")
                .create();

        Mockito.when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.mapEntityToDto(user)).thenReturn(userDto);

        //when:
        UserDto actual = userService.updateUser(user.getLogin(), userRequestDto);

        //then:
        Assertions.assertThat(actual.name()).isEqualTo(userRequestDto.name());
        Mockito.verify(userRepository).save(userCaptor.capture());
        Assertions.assertThat(userCaptor.getValue().getName()).isEqualTo("Bogdan");
    }

    @Test
    void updateUser_shouldThrowResponseStatusException() {
        //given:
        UserRequestDto user = Instancio.create(UserRequestDto.class);
        Mockito.when(userRepository.findByLogin(user.login())).thenReturn(Optional.empty());

        //when and then:
        Assertions.assertThatThrownBy(() -> userService.updateUser(user.login(), user))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }


    @Test
    void logInUser_shouldReturnUserDtoIfPasswordAndLoginAreCorrect() {
        //given:
        String userLogin = "Login";
        UserRequestDto userRequestDto = Instancio.of(UserRequestDto.class)
                .set(field(UserRequestDto::password), "xxx")
                .create();
        User user = Instancio.of(User.class)
                .set(field(User::getLogin), userLogin)
                .set(field(User::getHashPassword), "xxx")
                .create();
        UserDto userDto = Instancio.of(UserDto.class)
                .set(field(UserDto::login), userLogin)
                .create();

        Mockito.when(userRepository.findByLogin(userLogin)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.mapEntityToDto(user)).thenReturn(userDto);

        //when:
        UserDto actual = userService.logInUser(userLogin, userRequestDto);

        //then:
        Assertions.assertThat(actual).isEqualTo(userDto);
    }

    @Test
    void logInUser_shouldThrowResponseStatusException404() {
        //given:
        String userLogin = "Login";
        UserRequestDto user = Instancio.create(UserRequestDto.class);
        Mockito.when(userRepository.findByLogin(user.login())).thenReturn(Optional.empty());

        //when and then:
        Assertions.assertThatThrownBy(() -> userService.logInUser(userLogin, user))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    void logInUser_shouldThrowResponseStatusException401() {
        //given:
        String userLogin = "Login";
        UserRequestDto userRequestDto = Instancio.create(UserRequestDto.class);
        User user = Instancio.create(User.class);
        Mockito.when(userRepository.findByLogin(userLogin)).thenReturn(Optional.of(user));

        //when and then:
        Assertions.assertThatThrownBy(() -> userService.logInUser(userLogin, userRequestDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.resolve(401));
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        //given:
        String userLogin = "Login";
        User user = Instancio.create(User.class);
        Mockito.when(userRepository.findByLogin(userLogin)).thenReturn(Optional.of(user));

        //when:
        userService.deleteUser(userLogin);

        //then:
        Mockito.verify(userRepository).delete(userCaptor.capture());
        Assertions.assertThat(userCaptor.getValue()).isEqualTo(user);
    }

    @Test
    void deleteUser_shouldThrowResponseStatusException() {
        //given:
        String userLogin = "Login";
        Mockito.when(userRepository.findByLogin(userLogin)).thenReturn(Optional.empty());

        //when:
        Assertions.assertThatThrownBy(() -> userService.deleteUser(userLogin))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    void softDeleteUser_shouldDeleteUser() {
        //given:
        String userLogin = "Login";
        User user = Instancio.of(User.class)
                .set(field(User::getName),"Adam")
                .set(field(User::getLogin),"adam")
                .set(field(User::getHashPassword),"xxx")
                .set(field(User::getEmail),"adam@email.com")
                .set(field(User::getNewsletter),true)
                .create()                ;
        Mockito.when(userRepository.findByLogin(userLogin)).thenReturn(Optional.of(user));

        //when:
        userService.softDeleteUser(userLogin);

        //then:
        Mockito.verify(userRepository).save(userCaptor.capture());
        Assertions.assertThat(userCaptor.getValue().getName())
                .isEqualTo("****");
        Assertions.assertThat(userCaptor.getValue().getLogin())
                .isEqualTo("****");
        Assertions.assertThat(userCaptor.getValue().getHashPassword())
                .isEqualTo("***");
        Assertions.assertThat(userCaptor.getValue().getEmail())
                .startsWith("deleted-");
        Assertions.assertThat(userCaptor.getValue().getNewsletter())
                .isEqualTo(false);
    }

    @Test
    void softDeleteUser_shouldThrowResponseStatusException() {
        //given:
        String userLogin = "Login";
        Mockito.when(userRepository.findByLogin(userLogin)).thenReturn(Optional.empty());

        //when:
        Assertions.assertThatThrownBy(() -> userService.softDeleteUser(userLogin))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }
}