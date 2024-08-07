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
    private List<Challenge> challengeList;

    @NotBlank
    private String name;

    @Embedded
    private Coordinates coordinates;

    private String mountainRange;

    private String mountains;

    @NotBlank
    @Min(value = 0, message = "Szczyt nie może być wartością ujemną")
    private int height;

    private String description;

    private String guideNotes;

    @NotBlank
    private Integer score;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Version
    private Integer version;


    public Double[] getCoordinatesArray() {
        return (coordinates == null) ? null : new Double[]{coordinates.getLatitude(), coordinates.getLongitude()};
    }
}