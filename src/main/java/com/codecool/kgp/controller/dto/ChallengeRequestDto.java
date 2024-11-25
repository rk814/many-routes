package com.codecool.kgp.controller.dto;

import com.codecool.kgp.controller.validation.ChallengeUniqueName;
import com.codecool.kgp.controller.validation.UserBasic;
import com.codecool.kgp.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChallengeRequestDto(

        @NotBlank(message = "Pole 'name' jest obowiązkowe")
        @Size(max = 50, message = "Nazwa może mieć maksymalnie 50 znaków", groups = UserBasic.class)
        @ChallengeUniqueName
        String name,


        String description,

        @NotNull(message = "Pole 'status' jest obowiązkowe")
        Status status
) {
}
