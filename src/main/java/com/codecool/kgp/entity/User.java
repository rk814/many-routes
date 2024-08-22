package com.codecool.kgp.entity;

import com.codecool.kgp.controller.dto.UserRequestDto;
import com.codecool.kgp.entity.enums.Role;
import com.codecool.kgp.entity.geography.Coordinates;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private Integer version = 0;


    public User(String login, String hashPassword, String email, Role role) {
        this.login = login;
        this.hashPassword = hashPassword;
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "id", orphanRemoval = true)
    private List<UserChallenge> userChallenges = new ArrayList<>();


    public Double[] getCoordinatesArray() {
        return (coordinates == null) ? null : new Double[]{coordinates.getLatitude(), coordinates.getLongitude()};
    }

    public void updateUser(UserRequestDto dto) {
        setName(dto.name());
        setEmail(dto.email());
        setPhone(dto.phone());
        setNewsletter(dto.newsletter());
        if (dto.latitude() != null && dto.longitude() != null) {
            setCoordinates(new Coordinates(dto.latitude(), dto.longitude()));
        } else {
            setCoordinates(null);
        }
    }

    public void assignUserChallenge(UserChallenge userChallenge) {
        if (this.userChallenges==null) {
            this.userChallenges = new ArrayList<>();
        }
        this.userChallenges.add(userChallenge);
    }
}
