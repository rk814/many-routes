package com.codecool.kgp.repository;

import jakarta.persistence.*;
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
@Table(name = "user_summit")
public class UserSummit {

    @Id
    @EqualsAndHashCode.Include
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_challenge_id", referencedColumnName = "id")
    private UserChallenge userChallenge;

    private LocalDateTime conqueredAt;


    public UserSummit(LocalDateTime conqueredAt) {
        this.conqueredAt = LocalDateTime.now();
    }
}
