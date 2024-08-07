package com.codecool.kgp.entity;

import jakarta.persistence.*;
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
@Table(name = "user_challenge")
public class UserChallenge {

    @Id
    @EqualsAndHashCode.Include
    private UUID id = UUID.randomUUID();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="challenge_id", referencedColumnName = "id")
    private Challenge challenge;

    @Column(updatable = false)
    private LocalDateTime startedAt;

    @Column(updatable = false)
    private LocalDateTime finishedAt;

    private int score;

    @OneToMany(mappedBy = "id", orphanRemoval = true)
    private List<UserSummit> userSummitList;


    public UserChallenge() {
        this.startedAt = LocalDateTime.now();
        this.score = 0;
    }

    public UserChallenge(User user, Challenge challenge) {
        this.user = user;
        this.challenge = challenge;
        this.startedAt = LocalDateTime.now();
        this.score = 0;
    }

    public void assignUserSummit(UserSummit userSummit) {
        userSummitList.add(userSummit);
    }
}
