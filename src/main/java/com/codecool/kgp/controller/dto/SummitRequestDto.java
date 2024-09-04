package com.codecool.kgp.controller.dto;

import com.codecool.kgp.controller.validation.SummitUniqueName;
import com.codecool.kgp.controller.validation.UserBasic;
import jakarta.validation.constraints.*;

public record SummitRequestDto(

        @NotBlank(message = "Pole 'nazwa' jest obowiązkowe")
        @Size(max = 50, message = "Imię może mieć maksymalnie 50 znaków", groups = UserBasic.class)
        @SummitUniqueName
        String name,

        @NotNull(message = "Pole 'latitude' jest obowiązkowe")
        @Min(value = -180, message = "Długość geograficzna musi być większa lub równa 180", groups = UserBasic.class)
        @Max(value = 180, message = "Długość geograficzna musi być mniejsza lub równa 180", groups = UserBasic.class)
        @Digits(integer = 3, fraction = 8, message = "Współrzędne muszą posiadać maksymalnie 8 cyfr po przecinku", groups = UserBasic.class)
        Double latitude,

        @NotNull(message = "Pole 'longitude' jest obowiązkowe")
        @Min(value = -180, message = "Długość geograficzna musi być większa lub równa 180", groups = UserBasic.class)
        @Max(value = 180, message = "Długość geograficzna musi być mniejsza lub równa 180", groups = UserBasic.class)
        @Digits(integer = 3, fraction = 8, message = "Współrzędne muszą posiadać maksymalnie 8 cyfr po przecinku", groups = UserBasic.class)
        Double longitude,

        @Size(max = 50, message = "Imię może mieć maksymalnie 50 znaków", groups = UserBasic.class)
        String mountainRange,

        @Size(max = 50, message = "Imię może mieć maksymalnie 50 znaków", groups = UserBasic.class)
        String mountains,

        @Min(value = 0, message = "Wysokość musi być większa od 0", groups = UserBasic.class)
        int height,

        String description,

        String guideNotes,

        @NotNull(message = "Pole 'punkty' jest obowiązkowe")
        @Min(value = 0, message = "Wartość punktów musi być większa od 0", groups = UserBasic.class)
        Integer score,

        @NotBlank(message = "Pole 'status' jest obowiązkowe")
        String status
) {
}