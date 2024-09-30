package com.codecool.kgp.service;

import com.codecool.kgp.entity.CustomUserDetails;
import com.codecool.kgp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Slf4j
@Component
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
                    CustomUserDetails userDetails = new CustomUserDetails(
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
