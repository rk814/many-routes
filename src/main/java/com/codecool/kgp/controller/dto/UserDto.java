package com.codecool.kgp.controller.dto;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        UUID id,
        String login,
        String name,
        String email,
        Double[] coordinates,
        String phone,
        Boolean newsletter,
        LocalDateTime createAt,
        LocalDateTime deletedAt,
        String role
) {
}
