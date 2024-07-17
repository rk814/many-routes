package com.codecool.kgp.controller.dto;

import com.codecool.kgp.controller.validation.UserLogin;
import com.codecool.kgp.controller.validation.UserRegister;
import com.codecool.kgp.controller.validation.UserUpdate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDto(

        @NotBlank(groups = {UserRegister.class, UserLogin.class}, message = "Login nie może być pusty")
        @Size(max = 50, message = "Login może mieć maksymalnie 50 znaków")
        String login,

        @NotBlank(groups = {UserRegister.class, UserLogin.class}, message = "Hasło nie może być puste")
        @Size(max = 50, message = "Login może mieć maksymalnie 50 znaków")
        String password,

        @NotBlank(groups = {UserRegister.class, UserUpdate.class}, message = "E-mail nie może być puste")
        @Email
        @Size(max = 100, message = "E-mail może mieć maksymalnie 100 znaków")
        String email,

        @Size(max = 50, message = "Imię może mieć maksymalnie 50 znaków")
        String name,

        @Size(max = 100, message = "Numer telefonu może mieć maksymalnie 100 znaków")
        String phone,

        boolean newsletter,

        @Min(value = 0, message = "Szrokość geograficzna musi być większa od 0")
        Double latitude,

        @Min(value = 0, message = "Długość geograficzna musi być większa od 0")
        Double longitude
) {

}
