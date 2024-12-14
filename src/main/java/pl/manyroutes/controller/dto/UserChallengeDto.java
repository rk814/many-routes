package pl.manyroutes.controller.dto;

import pl.manyroutes.entity.enums.Status;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserChallengeDto(
        UUID id,
        UUID userId,

        UUID challengeId,
        String challengeDescription,
        String challengeName,
        Status challengeStatus,

        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        Integer score,
        Set<UserSummitDto> userSummitsSet
) {

}

