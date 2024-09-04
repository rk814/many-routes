package com.codecool.kgp.config;

import com.codecool.kgp.auth.JwtTokenFilter;
import com.codecool.kgp.service.JwtTokenService;
import com.codecool.kgp.service.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableConfigurationProperties({AuthConfigProperties.class})
@EnableMethodSecurity(jsr250Enabled = true)
@SecurityScheme(
        name = "jwtauth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@OpenAPIDefinition(security = {@SecurityRequirement(name = "jwtauth")})
public class SpringSecurityConfig {

    public static final String USER = "USER";
    public static final String ADMIN = "ADMIN";

    private final String[] URL_WHITELIST = {
            "/got/v1/auth/login",
            "/got/v1/auth/register",
            "/error",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationEntryPoint authenticationEntryPoint, JwtTokenFilter jwtTokenFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(autz ->
                        autz.requestMatchers(URL_WHITELIST).permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(eh -> eh.authenticationEntryPoint(authenticationEntryPoint))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //
//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//        UserDetails user = User.withUsername("user")
//                .password(passwordEncoder.encode("password"))
//                .roles(USER)
//                .build();
//        UserDetails admin = User.withUsername("admin")
//                .password(passwordEncoder.encode("admin"))
//                .roles(ADMIN)
//                .build();
//
//        return new InMemoryUserDetailsManager(user, admin);
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService(UserRepository userRepository) throws UsernameNotFoundException {
//        log.debug("Entering in findByLogin from userRepository");
//        return login -> userRepository.findByLogin(login)
//                .map(u -> new UserDetails() {
//                    @Override
//                    public Collection<? extends GrantedAuthority> getAuthorities() {
//                        return List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole()));
//                    }
//
//                    @Override
//                    public String getPassword() {
//                        return u.getHashPassword();
//                    }
//
//                    @Override
//                    public String getUsername() {
//                        return u.getLogin();
//                    }
//                })
//                .orElseThrow(() -> {
//                    log.error("User with login '{}' not found", login);
//                    return new UsernameNotFoundException("User not found");
//                });
//    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // default strength 10
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter(JwtTokenService jwtTokenService, UserDetailsService userDetailsService) {
        return new JwtTokenFilter(jwtTokenService, userDetailsService);
    }

    @Bean
    public JwtTokenService jwtTokenService(AuthConfigProperties authConfigProperties, UserService userService) {
        return new JwtTokenService(authConfigProperties, userService);
    }
}
