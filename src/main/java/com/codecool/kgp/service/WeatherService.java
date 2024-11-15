package com.codecool.kgp.service;

import com.codecool.kgp.config.WeatherApiProperties;
import com.codecool.kgp.controller.dto.weather.AstronomyDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;


@Slf4j
@Service
public class WeatherService {

    private final String API_KEY;
    private final String API_URL;
    private final String LANG = "pl";

    private final Gson gson = new Gson();

    private final WebClient webClient;


    public WeatherService(WebClient.Builder webClient, WeatherApiProperties properties) {
        this.API_KEY = properties.key();
        this.API_URL = properties.url();

        this.webClient = webClient.baseUrl(API_URL).build();
    }


    public JsonObject getCurrentWeather(double latitude, double longitude) {
        try {
            String json = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("current.json")
                            .queryParam("key", API_KEY)
                            .queryParam("q", latitude + "," + longitude)
                            .queryParam("lang", LANG)
                            .build())
                    .retrieve().bodyToMono(String.class).block();
            validateResponse(json);
            log.info("Current weather data fetched successfully");
            return mapToJsonObject(json);

        } catch (ResponseStatusException e) {
            log.error("Error fetching current weather data from weather server. Original status: {}, message: {}", e.getStatusCode(), e.getMessage(),e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching current weather data: {}", e.getMessage(),e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public JsonObject getWeatherForecast(double latitude, double longitude, int days) {
        try {
            String json = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("forecast.json")
                            .queryParam("key", API_KEY)
                            .queryParam("q", latitude + "," + longitude)
                            .queryParam("lang", LANG)
                            .queryParam("days", days)
                            .build())
                    .retrieve().bodyToMono(String.class).block();
            validateResponse(json);
            log.info("Forecast data fetched successfully");
            return mapToJsonObject(json);

        } catch (ResponseStatusException e) {
            log.error("Error fetching forecast data from weather server. Original status: {}, message: {}", e.getStatusCode(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching forecast weather data: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public AstronomyDto getCurrentAstronomy(double latitude, double longitude) {
        try {
            String json = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("astronomy.json")
                            .queryParam("key", API_KEY)
                            .queryParam("q", latitude + "," + longitude)
                            .queryParam("lang", LANG)
                            .build()).retrieve().bodyToMono(String.class).block();
            validateResponse(json);
            log.info("Astronomy data fetched successfully");
            return mapToAstronomyDto(json);

        } catch (ResponseStatusException e) {
            log.error("Error fetching astronomy data from weather server. Original status: {}, message: {}", e.getStatusCode(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching astronomy weather data: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private JsonObject mapToJsonObject(String json) {
        JsonElement jsonElement = JsonParser.parseString(json);
        return gson.fromJson(jsonElement, JsonObject.class);
    }

    private AstronomyDto mapToAstronomyDto(String response) throws IOException {
        JsonElement jsonElement = JsonParser.parseString(response);
        JsonElement astroJson = jsonElement.getAsJsonObject().get("astronomy").getAsJsonObject().get("astro");
        return gson.fromJson(astroJson, AstronomyDto.class);
    }

    private void validateResponse(String json) {
        if (json==null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty response from weather server");
        }
    }
}
