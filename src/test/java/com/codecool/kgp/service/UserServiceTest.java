package com.codecool.kgp.service;

import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.UserRequestDto;
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

    @Captor
    private ArgumentCaptor<User> userCaptor;

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
        Mockito.verify(userRepository).saveAndFlush(userCaptor.capture());
        Assertions.assertThat(userCaptor.getValue().getId()).isEqualTo(user.getId());
        Assertions.assertThat(userCaptor.getValue().getHashPassword()).isEqualTo(user.getHashPassword());
        Assertions.assertThat(userCaptor.getValue().getName()).isEqualTo(user.getName());
        Assertions.assertThat(userCaptor.getValue().getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(userCaptor.getValue().getLogin()).isEqualTo(user.getLogin());
        Assertions.assertThat(userCaptor.getValue().getPhone()).isEqualTo(user.getPhone());
        Assertions.assertThat(userCaptor.getValue().getNewsletter()).isEqualTo(user.getNewsletter());
        Assertions.assertThat(userCaptor.getValue().getCoordinates().getLatitude())
                .isEqualTo(user.getCoordinates().getLatitude());
        Assertions.assertThat(userCaptor.getValue().getCoordinates().getLongitude())
                .isEqualTo(user.getCoordinates().getLongitude());
    }
}