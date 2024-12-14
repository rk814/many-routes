package pl.manyroutes.service;

import pl.manyroutes.auth.CustomUserDetails;
import pl.manyroutes.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Entering in findByLogin from userRepository");
        return userRepository.findByLogin(username)
                .map(u -> {
                    UserDetails userDetails = new CustomUserDetails(
                            u.getId(),
                            u.getLogin(),
                            u.getHashPassword(),
                            List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole()))
                    );
//                    UserDetails userDetails = new UserDetails() {
//                        @Override
//                        public Collection<? extends GrantedAuthority> getAuthorities() {
//                            return List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole()));
//                        }
//
//                        @Override
//                        public String getPassword() {
//                            return u.getHashPassword();
//                        }
//
//                        @Override
//                        public String getUsername() {
//                            return u.getLogin();
//                        }
//                    };
                    log.info("User details loaded successfully for login '{}'",username);
                    return userDetails;
                })
                .orElseThrow(() -> {
                    log.error("User with login '{}' not found", username);
                    return new UsernameNotFoundException("User not found");
                });
    }
}
