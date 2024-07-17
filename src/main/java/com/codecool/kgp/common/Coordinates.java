package com.codecool.kgp.common;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@AttributeOverride(name="latitude", column=@Column(name="coords_latitude"))
@AttributeOverride(name="longitude", column=@Column(name="coords_longitude"))
public class Coordinates {

    @Min(value = 0, message = "Szrokość geograficzna musi być większa od 0")
    private Double latitude;

    @Min(value = 0, message = "Długość geograficzna musi być większa od 0")
    private Double longitude;
}
