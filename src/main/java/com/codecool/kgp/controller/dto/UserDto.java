package com.codecool.kgp.controller.dto;
import java.util.UUID;

public record UserDto(
        UUID id,
        String login,
        String name,
        String email,
        double[] coordinates,
        String phone,
        boolean newsletter,
        String role
) {
}
