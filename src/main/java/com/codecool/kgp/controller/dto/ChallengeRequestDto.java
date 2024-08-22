package com.codecool.kgp.controller.dto;

import com.codecool.kgp.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChallengeRequestDto(
        @NotBlank
        String name,

        String description,

        @NotNull
        Status status

        //TODO
) {
}
