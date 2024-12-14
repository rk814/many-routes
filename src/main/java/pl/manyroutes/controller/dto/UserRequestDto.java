package pl.manyroutes.controller.dto;

import jakarta.validation.constraints.*;

public record UserRequestDto(

        @NotBlank(message = "E-mail nie może być pusty")
        @Email()
        @Size(max = 100, message = "E-mail może mieć maksymalnie 100 znaków")
        String email,

        @Size(max = 50, message = "Imię może mieć maksymalnie 50 znaków")
        String name,

        @Size(max = 100, message = "Numer telefonu może mieć maksymalnie 100 znaków")
        String phone,

        Boolean newsletter,

        @Min(value = -180, message = "Szerokość geograficzna musi być większa lub równa 180")
        @Max(value = 180, message = "Szerokość geograficzna musi być mniejsza lub równa 180")
        @Digits(integer = 3, fraction = 8, message = "Współrzędne muszą posiadać maksymalnie 8 cyfr po przecinku")
        Double latitude,

        @Min(value = -180, message = "Długość geograficzna musi być większa lub równa 180")
        @Max(value = 180, message = "Długość geograficzna musi być mniejsza lub równa 180")
        @Digits(integer = 3, fraction = 8, message = "Współrzędne muszą posiadać maksymalnie 8 cyfr po przecinku")
        Double longitude
) {

}
