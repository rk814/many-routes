package com.codecool.kgp.controller.dto;

import com.codecool.kgp.controller.validation.UserLogin;
import com.codecool.kgp.controller.validation.UserRegister;
import com.codecool.kgp.controller.validation.UserUpdate;
import jakarta.validation.constraints.*;

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

        @Min(value = -180, message = "Szerokość geograficzna musi być większa lub równa 180")
        @Max(value = 180, message = "Szerokość geograficzna musi być mniejsza lub równa 180")
        @Digits(integer = 3, fraction = 3, message = "Współrzędne muszą posiadać maksymalnie 5 cyfr po przecinku")
        Double latitude,

        @Min(value = -180, message = "Długość geograficzna musi być większa lub równa 180")
        @Max(value = 180, message = "Długość geograficzna musi być mniejsza lub równa 180")
        @Digits(integer = 3, fraction = 3, message = "Współrzędne muszą posiadać maksymalnie 5 cyfr po przecinku")
        Double longitude
) {

}
