package pl.manyroutes.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserChallengeSimpleDto(
        UUID id,
        UUID userId,
        String challengeName,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        int score
) {
}
