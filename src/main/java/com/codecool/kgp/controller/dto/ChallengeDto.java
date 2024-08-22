package com.codecool.kgp.controller.dto;

import com.codecool.kgp.entity.enums.Status;

import java.util.List;
import java.util.UUID;

public record ChallengeDto(
        UUID id,
        String name,
        String description,
        Status status,
        List<SummitSimpleDto> summits
) {
}