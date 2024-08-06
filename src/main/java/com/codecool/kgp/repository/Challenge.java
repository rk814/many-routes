package com.codecool.kgp.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Challenge {

    @Id
    private final UUID id = UUID.randomUUID();

    private String name;

    @ManyToMany
    private List<Summit> summitList;
}
