package com.codecool.kgp.controller.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserChallengeDao (
        UUID id,
        UUID userId,
        ChallengeDto challengeDto,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        Integer score,
        List<UserSummitDto> userSummitList
) {
    public record UserSummitDto(
            UUID id,
            LocalDateTime conqueredAt
    ) {
    }

    public record ChallengeDto(
            UUID id,
            String name
    ) {
    }
}

