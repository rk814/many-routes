package com.codecool.kgp.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record JwtTokenRequestDto(
        @NotBlank
        String username,

        @NotBlank
        String password
) {
}
