package com.codecool.kgp.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserChallengeSimpleDao(
        UUID id,
        UUID userId,
        String name,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        int score
) {
}
