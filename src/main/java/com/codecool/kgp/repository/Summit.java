package com.codecool.kgp.repository;

import jakarta.persistence.*;
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
@Table(name="summit")
public class Summit {

    @Id
    @EqualsAndHashCode.Include
    private final UUID id = UUID.randomUUID();

    private String name;

    private Integer score;

    @ManyToMany(mappedBy = "summitList")
    private List<Challenge> challengeList;

    @Version
    private Integer version;

}
