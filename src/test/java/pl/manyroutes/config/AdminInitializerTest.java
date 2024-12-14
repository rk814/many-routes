package pl.manyroutes.config;

import pl.manyroutes.controller.dto.RegistrationRequestDto;
import pl.manyroutes.entity.User;
import pl.manyroutes.repository.UserRepository;
import pl.manyroutes.service.AuthService;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.DefaultApplicationArguments;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AdminInitializerTest {

    @Mock
    UserRepository userRepository;

    @Mock
    AuthService authService;

    @InjectMocks
    AdminInitializer initializer;


    @Test
    void initializer_shouldAddDefaultAdminToDb_whenDbUserTableIsEmpty() throws Exception {
        //given:
        ApplicationArguments args = new DefaultApplicationArguments(new String[]{});
        Mockito.when(userRepository.findAll()).thenReturn(List.of());

        //when:
        ApplicationRunner runner = initializer.initializer();
        runner.run(args);

        //then:
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        ArgumentCaptor<RegistrationRequestDto> captor = ArgumentCaptor.forClass(RegistrationRequestDto.class);
        Mockito.verify(authService, Mockito.times(1)).registerNewAdmin(captor.capture());
        Assertions.assertThat(captor.getValue().login()).isEqualTo("admin");
        Assertions.assertThat(captor.getValue().password()).isEqualTo("admin");
        Assertions.assertThat(captor.getValue().email()).isEqualTo("not-an-email@example.com");
    }

    @Test
    void initializer_shouldDoNothing_whenDbContainAnyUser() throws Exception {
        //given:
        ApplicationArguments args = new DefaultApplicationArguments(new String[]{});
        User user = Instancio.create(User.class);
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));

        //when:
        ApplicationRunner runner = initializer.initializer();
        runner.run(args);

        //then:
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        ArgumentCaptor<RegistrationRequestDto> captor = ArgumentCaptor.forClass(RegistrationRequestDto.class);
        Mockito.verify(authService, Mockito.times(0)).registerNewAdmin(captor.capture());
    }
}