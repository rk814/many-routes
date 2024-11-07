package com.codecool.kgp.controller.dto;

import java.util.UUID;

public record SummitSimpleDto(
        UUID id,
        String name,
        String mountainRange,
        String mountainChain,
        int height,
        String status
) {
}
