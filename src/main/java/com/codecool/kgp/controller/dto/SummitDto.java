package com.codecool.kgp.controller.dto;

import com.codecool.kgp.entity.Challenge;

import java.util.List;
import java.util.UUID;

public record SummitDto(
        UUID id,
        List<ChallengeSimpleDto> challangeList,
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
