package com.codecool.kgp.repository;

import com.codecool.kgp.repository.geography.Coordinates;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

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
    @Size(min = 0, message = "Szczyt nie może być wartością ujemną")
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

}
