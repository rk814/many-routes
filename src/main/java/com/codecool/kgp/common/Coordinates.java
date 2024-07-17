package com.codecool.kgp.common;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
@AttributeOverride(name="latitude", column=@Column(name="coords_latitude"))
@AttributeOverride(name="longitude", column=@Column(name="coords_longitude"))
public class Coordinates {

    private double latitude;

    private double longitude;
}
