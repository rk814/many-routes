package com.codecool.kgp.controller.dto.weather;

import com.google.gson.annotations.SerializedName;

public record AstronomyDto(
        String sunrise,
        String sunset,
        String moonrise,
        String moonset,
        @SerializedName("moon_phase") String phase,
        int moon_illumination
) {
    public AstronomyDto(String sunrise, String sunset, String moonrise, String moonset, String phase, int moon_illumination) {
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.moonrise = moonrise;
        this.moonset = moonset;
        this.phase = translatePhaseName(phase);
        this.moon_illumination = moon_illumination;

    }

    private String translatePhaseName(String name) {
        return switch (name) {
            case "New Moon" -> "Nów";
            case "Waxing Crescent" -> "Rosnący Sierp";
            case "First Quarter" -> "Pierwsza Kwadra";
            case "Waxing Gibbous" -> "Rosnący Garb";
            case "Full Moon" -> "Pełnia";
            case "Waning Gibbous" -> "Znikający Garb";
            case "Third Quarter" -> "Trzecia Kwadra";
            case "Waning Crescent" -> "Znikający Sierp";
            default -> name;
        };
    }
}
