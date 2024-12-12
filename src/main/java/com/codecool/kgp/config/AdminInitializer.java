package com.codecool.kgp.config;

import com.codecool.kgp.controller.dto.RegistrationRequestDto;
import com.codecool.kgp.repository.UserRepository;
import com.codecool.kgp.service.AuthService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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
