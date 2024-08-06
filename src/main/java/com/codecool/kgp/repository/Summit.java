package com.codecool.kgp.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Summit {

    @Id
    private final UUID id = UUID.randomUUID();

    private String name;
}
