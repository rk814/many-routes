package com.codecool.kgp.controller.dto;

import com.codecool.kgp.repository.Challenge;
import com.codecool.kgp.repository.UserSummit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserChallengeDao (
        UUID id,
        UUID userId,
        Challenge challenge, //??? or press fit all fields
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        Integer score,
        List<UserSummit> userSummitList
) {}

