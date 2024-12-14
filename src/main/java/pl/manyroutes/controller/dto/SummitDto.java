package pl.manyroutes.controller.dto;

import java.util.Set;
import java.util.UUID;

public record SummitDto(
        UUID id,
        Set<ChallengeSimpleDto> challengesSet,
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
