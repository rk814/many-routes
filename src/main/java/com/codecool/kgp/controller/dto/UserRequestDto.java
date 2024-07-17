package com.codecool.kgp.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
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
        String email,

        @Size(max=50, message = "Imię może mieć maksymalnie 50 znaków")
        String name,

        @Size(max=100, message = "Numer telefonu może mieć maksymalnie 100 znaków")
        String phone,

        boolean newsletter,

        @Min(value = 0, message = "Wartość musi być większa od 0")
        Double latitude,

        @Min(value = 0, message = "Wartość musi być większa od 0")
        Double longitude
) {

}
