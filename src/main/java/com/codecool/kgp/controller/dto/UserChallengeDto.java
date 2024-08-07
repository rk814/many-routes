package com.codecool.kgp.controller.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserChallengeDto(
        UUID id,
        UUID userId,
        ChallengeDto challengeDto,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        Integer score,
        List<UserSummitDto> userSummitList
) {

}

