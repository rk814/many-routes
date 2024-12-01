package com.codecool.kgp.service;

import com.codecool.kgp.controller.dto.RegistrationRequestDto;
import com.codecool.kgp.controller.dto.UserChallengeSimpleDto;
import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.entity.User;
import com.codecool.kgp.errorhandling.DuplicateEntryException;
import com.codecool.kgp.mappers.UserMapper;
import com.codecool.kgp.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.codecool.kgp.entity.enums.Role.USER;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;


    @InjectMocks
    private AuthService authService;


    @Test
    void registerNewUser_shouldSaveNewUserAndReturnDto() {
        //given:
        RegistrationRequestDto dto = Instancio.create(RegistrationRequestDto.class);
        User newUser = Instancio.of(User.class)
                .generate(field(User::getCreatedAt), gen->gen.temporal().localDateTime().past())
                .create();
        String hashPassword = "$2a$10$iUmwmDFunQg08R/FuzhAyelka9UhHLCWGsojFiKQgN7UkBpPt4qdK";
        Mockito.when(passwordEncoder.encode(dto.password())).thenReturn(hashPassword);
        Mockito.when(userRepository.save(any())).thenReturn(newUser);
        AtomicReference<UserDto> result = new AtomicReference<>();
        Mockito.when(userMapper.mapEntityToDto(newUser)).thenAnswer(invocation ->
                {
                    User user = invocation.getArgument(0);
                    Set<UserChallengeSimpleDto> userChallengesDto = user.getUserChallengesSet().stream()
                            .map(uch -> new UserChallengeSimpleDto(uch.getId(), user.getId(), uch.getChallenge().getName(),
                                    uch.getStartedAt(), uch.getFinishedAt(), uch.getScore())).collect(Collectors.toSet());
                    result.set(new UserDto(user.getId(), user.getLogin(), user.getName(), user.getEmail(),
                            user.getCoordinatesArray(), user.getPhone(), user.getNewsletter(), user.getCreatedAt(),
                            user.getDeletedAt(), user.getRole().toString(), userChallengesDto));
                    return result.get();
                }
        );

        //when:
        UserDto actual = authService.registerNewUser(dto);

        //then:
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(passwordEncoder).encode(passwordCaptor.capture());
        Assertions.assertThat(passwordCaptor.getValue()).isEqualTo(dto.password());
        ArgumentCaptor<User> saveCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(saveCaptor.capture());
        Assertions.assertThat(saveCaptor.getValue().getLogin()).isEqualTo(dto.login());
        Assertions.assertThat(saveCaptor.getValue().getHashPassword()).isEqualTo(hashPassword);
        Assertions.assertThat(saveCaptor.getValue().getEmail()).isEqualTo(dto.email());
        Assertions.assertThat(saveCaptor.getValue().getRole()).isEqualTo(USER);

        Assertions.assertThat(actual).extracting(UserDto::id).isEqualTo(newUser.getId());
        Assertions.assertThat(actual).extracting(UserDto::login).isEqualTo(newUser.getLogin());
        Assertions.assertThat(actual).extracting(UserDto::email).isEqualTo(newUser.getEmail());
        Assertions.assertThat(actual).extracting(UserDto::role).isEqualTo(newUser.getRole().toString());
        Assertions.assertThat(actual).extracting(UserDto::createAt).matches(d-> d.isBefore(LocalDateTime.now()));
    }

    @Test
    void registerNewUser_shouldThrowDuplicateEntryException() {
        //given:
        RegistrationRequestDto dto = Instancio.create(RegistrationRequestDto.class);
        Mockito.when(userRepository.save(any())).thenThrow(new DataIntegrityViolationException("Duplication!"));

        //when:
        Throwable actual = Assertions.catchThrowable(()-> authService.registerNewUser(dto));

        //then:
        Assertions.assertThat(actual).isInstanceOf(DuplicateEntryException.class)
                .hasMessageContaining("już");
    }

}