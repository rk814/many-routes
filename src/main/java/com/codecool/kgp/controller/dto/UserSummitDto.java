package com.codecool.kgp.controller.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserSummitDto(
        UUID id,
        UUID summitId,
        UUID userChallengeId,
        LocalDateTime conqueredAt,

        // merge data form Summit
        Set<ChallengeSimpleDto> challangesSet,
        String name,
        Double[] coordinates,
        String mountainRange,
        String mountainsChain,
        int height,
        String description,
        String guideNotes,
        Integer score,
        String status
) {
}
