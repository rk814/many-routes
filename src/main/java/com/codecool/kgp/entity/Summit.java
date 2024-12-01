package com.codecool.kgp.entity;

import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.entity.geography.Coordinates;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "summit")
public class Summit {

    @Id
    private final UUID id = UUID.randomUUID();

    @NotBlank
    @EqualsAndHashCode.Include
    @Column(unique = true)
    private String name;

    @Embedded
    private Coordinates coordinates;

    private String mountainRange;

    private String mountainChain;

    @Min(value = 0, message = "Szczyt nie może być wartością ujemną")
    private Integer height;

    @Size(max = 4000, message = "Opis nie może przekroczyć 4000 znaków") // default 256
    private String description;

    @Size(max = 4000, message = "Opis nie może przekroczyć 4000 znaków") // default 256
    private String guideNotes;

    @NotNull
    private Integer score;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Version
    private Integer version = 0;


    public Summit(String name, Coordinates coordinates, String mountainRange, String mountains, int height, String description, String guideNotes, Integer score, Status status) {
        this.name = name;
        this.coordinates = coordinates;
        this.mountainRange = mountainRange;
        this.mountainChain = mountains;
        this.height = height;
        this.description = description;
        this.guideNotes = guideNotes;
        this.score = score;
        this.status = status;
    }

    @ManyToMany(mappedBy = "summitsSet")
    private Set<Challenge> challengesSet = new HashSet<>();


    public Double[] getCoordinatesArray() {
        return (coordinates == null) ? null : new Double[]{coordinates.getLatitude(), coordinates.getLongitude()};
    }

    public void addChallenge(Challenge challenge) {
        challengesSet.add(challenge);
    }

    public void removeChallenge(Challenge challenge) {
        this.challengesSet = challengesSet.stream().filter(ch -> !ch.equals(challenge)).collect(Collectors.toSet());
    }

    public void updateSummit(Summit summit) {
        this.name = summit.getName();
        this.coordinates = summit.getCoordinates();
        this.mountainRange = summit.getMountainRange();
        this.mountainChain = summit.getMountainChain();
        this.height = summit.getHeight();
        this.description = summit.getDescription();
        this.guideNotes = summit.getGuideNotes();
        this.score = summit.getScore();
        this.status = summit.getStatus();
    }
}
