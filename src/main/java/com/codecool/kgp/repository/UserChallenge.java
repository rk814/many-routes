package com.codecool.kgp.repository;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class UserChallenge {

    @Id
    @EqualsAndHashCode.Include
    private UUID id = UUID.randomUUID();

    @NotBlank
    private UUID userId; // reference to user id

    @NotBlank
    private UUID challengeId; //reference to challenge id

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private int score;

    @OneToMany
    private List<UserSummit> userSummitList;

    public UserChallenge() {
        this.startedAt = LocalDateTime.now();
        this.score = 0;
    }
}
