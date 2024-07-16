package com.codecool.kgp.repository;

import com.codecool.kgp.common.Coordinates;
import com.codecool.kgp.common.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class User {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @NotBlank
    @Column(unique = true)
    @Size(max=50, message = "Login może mieć maksymalnie 50 znaków")
    private String login;

    @NotBlank
    @Column(unique = true)
    private String hashPassword;

    @NotBlank
    @Size(max=50, message = "Imię może mieć maksymalnie 50 znaków")
    private String name;

    @NotBlank
    @Column(unique = true)
    @Size(max=100, message = "E-mail może mieć maksymalnie 100 znaków")
    private String email;

    @Embedded
    private Coordinates coordinates;

    @Size(max=100, message = "Numer telefonu może mieć maksymalnie 100 znaków")
    private String phone;

    private boolean newsletter;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Version
    private int version;
}
