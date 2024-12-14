package pl.manyroutes.controller.dto;

import pl.manyroutes.entity.enums.Status;

import java.util.UUID;

public record ChallengeSimpleDto(
        UUID id,
        String name,
        Status status
) {
}
