package com.codecool.kgp.entity;

import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.entity.geography.Coordinates;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "summit")
public class Summit {

    @Id
    @EqualsAndHashCode.Include
    private final UUID id = UUID.randomUUID();

    @ManyToMany(mappedBy = "summitList")
    private List<Challenge> challengeList = new ArrayList<>();

    @NotBlank
    @Column(unique = true)
    private String name;

    @Embedded
    private Coordinates coordinates;

    private String mountainRange;

    private String mountains;

    @Min(value = 0, message = "Szczyt nie może być wartością ujemną")
    private int height;

    private String description;

    private String guideNotes;

    @NotNull
    private Integer score;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Version
    private Integer version;


    public Summit(String name, Coordinates coordinates, String mountainRange, String mountains, int height, String description, String guideNotes, Integer score, Status status) {
        this.name = name;
        this.coordinates = coordinates;
        this.mountainRange = mountainRange;
        this.mountains = mountains;
        this.height = height;
        this.description = description;
        this.guideNotes = guideNotes;
        this.score = score;
        this.status = status;
    }

    public Double[] getCoordinatesArray() {
        return (coordinates == null) ? null : new Double[]{coordinates.getLatitude(), coordinates.getLongitude()};
    }
}
