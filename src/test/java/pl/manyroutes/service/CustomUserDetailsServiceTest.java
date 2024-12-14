package pl.manyroutes.service;

import pl.manyroutes.entity.User;
import pl.manyroutes.entity.enums.Role;
import pl.manyroutes.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;

class CustomUserDetailsServiceTest {

    private final UserRepository userRepository = Mockito.mock();

    private final CustomUserDetailsService service = new CustomUserDetailsService(userRepository);


    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        //given:
        String login = "login";
        User user = Instancio.of(User.class)
                .set(field(User::getLogin), login)
                .set(field(User::getRole), Role.USER)
                .create();
        Mockito.when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        //when:
        UserDetails actual = service.loadUserByUsername(login);

        //then:
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).extracting(UserDetails::getUsername).isEqualTo("login");
        Assertions.assertThat(actual).extracting(UserDetails::getAuthorities)
                .isEqualTo(List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_shouldThrowUsernameNotFoundException() {
        //given:
        String login = "login";
        Mockito.when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        //when:
        Throwable actual = Assertions.catchThrowable(() -> service.loadUserByUsername(login));

        //then:
        Assertions.assertThat(actual).isInstanceOf(UsernameNotFoundException.class);
    }
}