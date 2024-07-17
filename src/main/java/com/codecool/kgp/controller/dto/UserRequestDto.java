package com.codecool.kgp.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDto(
        @NotBlank(message = "Login nie może być pusty")
        @Size(max = 50, message = "Login może mieć maksymalnie 50 znaków")
        String login,
        @NotBlank(message = "Hasło nie może być puste")
        @Size(max = 50, message = "Login może mieć maksymalnie 50 znaków")
        String password,
        @NotBlank(message = "E-mail nie może być puste")
        @Email
        @Size(max = 100, message = "E-mail może mieć maksymalnie 100 znaków")
        String email
) {

}
