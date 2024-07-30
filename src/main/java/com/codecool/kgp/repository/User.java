package com.codecool.kgp.repository;

import com.codecool.kgp.repository.geography.Coordinates;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "actor")
public class User {

    @Id
    @EqualsAndHashCode.Include
    private UUID id = UUID.randomUUID();

    @NotBlank
    @Column(unique = true)
    @Size(max=50, message = "Login może mieć maksymalnie 50 znaków")
    private String login;

    @NotBlank
    private String hashPassword;

    @Size(max=50, message = "Imię może mieć maksymalnie 50 znaków")
    private String name;

    @NotBlank
    @Email
    @Column(unique = true)
    @Size(max=100, message = "E-mail może mieć maksymalnie 100 znaków")
    private String email;

    @Embedded
    private Coordinates coordinates;

    @Size(max=100, message = "Numer telefonu może mieć maksymalnie 100 znaków")
    private String phone;

    private Boolean newsletter;

    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Version
    private Integer version;


    public User(String login, String hashPassword, String email, Role role) {
        this.login = login;
        this.hashPassword = hashPassword;
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    public Double[] getCoordinatesArray() {
        if (coordinates==null) {
            return null;
        }
        return new Double[]{coordinates.getLatitude(), coordinates.getLongitude()};
    }
}
