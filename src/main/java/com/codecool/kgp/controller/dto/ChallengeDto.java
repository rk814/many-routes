package com.codecool.kgp.controller.dto;

import com.codecool.kgp.entity.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChallengeDto(
        UUID id,
        String name,
        String description,
        Status status,
        Set<SummitSimpleDto> summitsSet
) {
}