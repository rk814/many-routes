package com.codecool.kgp.controller.dto;

import com.codecool.kgp.entity.enums.Status;

import java.util.UUID;

public record ChallengeSimpleDto(
        UUID id,
        String name,
        Status status
) {
}
