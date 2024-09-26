package com.codecool.kgp.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Column(updatable = false)
    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private int score;

    @Version
    private Integer version = 0;


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

    @OneToMany(mappedBy = "userChallenge", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<UserSummit> userSummitList = new ArrayList<>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", referencedColumnName = "id")
    private Challenge challenge;


    public void assignUserSummit(UserSummit userSummit) {
        userSummitList.add(userSummit);
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        if (this.finishedAt == null) {
            this.finishedAt = finishedAt;
        } else {
            throw new IllegalStateException("This value cannot be changed");
        }
    }
}
