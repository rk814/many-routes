package com.codecool.kgp.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class UserSummit {

    @Id
    @EqualsAndHashCode.Include
    private UUID id = UUID.randomUUID();

    @NotBlank
    private UUID userId; // reference to user id

    @NotBlank
    private UUID challengeId; //reference to challenge id

    @NotBlank
    private UUID summitId; // reference to summit id

    private LocalDateTime conqueredAt;

    public UserSummit(LocalDateTime conqueredAt) {
        this.conqueredAt = LocalDateTime.now();
    }
}
