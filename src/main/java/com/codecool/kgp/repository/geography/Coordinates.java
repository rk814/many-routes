package com.codecool.kgp.repository.geography;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
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

    // TODO better digits with fraction exactly 5 (not max)
    @Min(value = -180, message = "Szrokość geograficzna musi być większa lub równa 180")
    @Max(value = 180, message = "Szrokość geograficzna musi być mniejsza lub równa 180")
    @Digits(integer = 3, fraction = 8, message = "Współrzędne muszą posiadać maksymalnie 8 cyfr po przecinku")
    private Double latitude;

    @Min(value = -180, message = "Szrokość geograficzna musi być większa lub równa 180")
    @Max(value = 180, message = "Szrokość geograficzna musi być mniejsza lub równa 180")
    @Digits(integer = 3, fraction = 8, message = "Współrzędne muszą posiadać maksymalnie 8 cyfr po przecinku")
    private Double longitude;
}
