package com.codecool.kgp.controller.dto.jwt;

import jakarta.validation.constraints.NotBlank;

public record JwtTokenRequestDto(
        @NotBlank
        String login,

        @NotBlank
        String password
) {
}
