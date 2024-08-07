package com.codecool.kgp.controller.dto;

import com.codecool.kgp.entity.Challenge;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserSummitDto(
        UUID id,
        UUID summitId,
        UUID challengeId,
        LocalDateTime conqueredAt,

        // merge data form Summit
        List<Challenge> challangeList,
        String name,
        Double[] coordinates,
        String mountainRange,
        String mountains,
        int height,
        String description,
        String guideNotes,
        Integer score,
        String status
) {
}
