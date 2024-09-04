package com.codecool.kgp.controller.dto.jwt;

import java.util.UUID;

public record JwtTokenResponseDto(
        String token,
        UUID id,
        String login,
        String name,
        String role
) {
}
