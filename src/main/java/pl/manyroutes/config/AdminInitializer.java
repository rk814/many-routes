package pl.manyroutes.config;

import pl.manyroutes.controller.dto.RegistrationRequestDto;
import pl.manyroutes.repository.UserRepository;
import pl.manyroutes.service.AuthService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AdminInitializer {

    private UserRepository userRepository;

    private AuthService authService;


    public AdminInitializer(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Bean
    @Profile("!test")
    public ApplicationRunner initializer() {
        return args -> {
            if (userRepository.findAll().isEmpty()) {
                var defaultAdminDto = new RegistrationRequestDto(
                        "admin",
                        "admin",
                        "not-an-email@example.com"
                );

                authService.registerNewAdmin(defaultAdminDto);
            }
        };
    }
}
