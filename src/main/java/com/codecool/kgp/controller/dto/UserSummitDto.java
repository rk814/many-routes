package com.codecool.kgp.controller.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserSummitDto(
        UUID id,
        UUID summitId,
        UUID userChallengeId,
        LocalDateTime conqueredAt,

        // merge data form Summit
        List<ChallengeSimpleDto> challangesList,
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
