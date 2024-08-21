package com.codecool.kgp.controller.dto;

import com.codecool.kgp.controller.validation.UserBasic;
import com.codecool.kgp.controller.validation.UserLogin;
import com.codecool.kgp.controller.validation.UserRegister;
import com.codecool.kgp.controller.validation.UserUpdate;
import jakarta.validation.constraints.*;

public record UserRequestDto(

        @NotBlank(message = "Login nie może być pusty")
        @Size(max = 50, message = "Login może mieć maksymalnie 50 znaków", groups = UserBasic.class)
        String login,

        @NotBlank(message = "Hasło nie może być puste")
        @Size(max = 50, message = "Login może mieć maksymalnie 50 znaków", groups = UserBasic.class)
        String password,

        @NotBlank(message = "E-mail nie może być puste")
        @Email(groups = UserBasic.class)
        @Size(max = 100, message = "E-mail może mieć maksymalnie 100 znaków", groups = UserBasic.class)
        String email,

        @Size(max = 50, message = "Imię może mieć maksymalnie 50 znaków", groups = UserBasic.class)
        String name,

        @Size(max = 100, message = "Numer telefonu może mieć maksymalnie 100 znaków", groups = UserBasic.class)
        String phone,

        Boolean newsletter,

        @Min(value = -180, message = "Szerokość geograficzna musi być większa lub równa 180", groups = UserBasic.class)
        @Max(value = 180, message = "Szerokość geograficzna musi być mniejsza lub równa 180", groups = UserBasic.class)
        @Digits(integer = 3, fraction = 8, message = "Współrzędne muszą posiadać maksymalnie 8 cyfr po przecinku", groups = UserBasic.class)
        Double latitude,

        @Min(value = -180, message = "Długość geograficzna musi być większa lub równa 180", groups = UserBasic.class)
        @Max(value = 180, message = "Długość geograficzna musi być mniejsza lub równa 180", groups = UserBasic.class)
        @Digits(integer = 3, fraction = 8, message = "Współrzędne muszą posiadać maksymalnie 8 cyfr po przecinku", groups = UserBasic.class)
        Double longitude
) {

}
