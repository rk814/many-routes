package com.codecool.kgp.controller.dto;

import java.util.List;
import java.util.UUID;

public record SummitDto(
        UUID id,
        List<ChallengeSimpleDto> challengesList,
        String name,
        Double[] coordinates,
        String mountainRange,
        String mountainChain,
        Integer height,
        String description,
        String guideNotes,
        Integer score,
        String status
) {
}
