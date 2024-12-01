package com.codecool.kgp.entity;

import com.codecool.kgp.entity.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Challenge {

    @Id
    private final UUID id = UUID.randomUUID();

    @NotBlank
    @EqualsAndHashCode.Include
    @Column(unique = true)
    private String name;

    @Size(max = 4000, message = "Opis nie może przekroczyć 4000 znaków") // default 256
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Version
    private Integer version = 0;


    public Challenge(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @ManyToMany
    private Set<Summit> summitsSet = new HashSet<>();


    public void addSummit(Summit summit) {
        summitsSet.add(summit);
    }

    public void removeSummit(Summit summit) {
        this.summitsSet = summitsSet.stream().filter(s -> !s.equals(summit)).collect(Collectors.toSet());
    }

    public void updateChallenge(Challenge challenge) {
        this.name = challenge.getName();
        this.description = challenge.getDescription();
        this.status = challenge.getStatus();
    }
}
