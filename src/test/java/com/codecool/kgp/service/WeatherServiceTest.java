package com.codecool.kgp.service;

import com.codecool.kgp.config.WeatherApiProperties;
import com.codecool.kgp.controller.dto.weather.AstronomyDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.function.Function;


@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    private WeatherService weatherService;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    WebClient webClient;

    @SuppressWarnings("rawtypes")
    @Mock
    WebClient.RequestHeadersSpec requestHeadersSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    WebClient.ResponseSpec responseSpec;


    @BeforeEach
    void setUp() {
        Mockito.when(webClientBuilder.baseUrl(Mockito.anyString())).thenReturn(webClientBuilder);
        Mockito.when(webClientBuilder.build()).thenReturn(webClient);

        this.weatherService = new WeatherService(webClientBuilder, new WeatherApiProperties("key", "url"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void getCurrentWeather_shouldReturnJson() throws JsonProcessingException {
        // given:
        Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(Mockito.any(Function.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        String json = "{\"msg\":\"Response message\"}";
        Mockito.when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(json));

        // when:
        JsonObject actual = weatherService.getCurrentWeather(12.3, 45.6);

        // then:
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.get("msg").getAsString()).isEqualTo("Response message");
    }

    @SuppressWarnings("unchecked")
    @Test
    void getCurrentWeather_shouldThrow500_whenExternalServerReturnErrorResponse() {
        // given:
        Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(Mockito.any(Function.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        Mockito.when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(
                new ResponseStatusException(HttpStatus.valueOf(500), "error")));

        // when:
        Throwable actual = Assertions.catchThrowable(() -> weatherService.getCurrentWeather(12.3, 45.6));

        // then:
        Assertions.assertThat(actual)
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> (ResponseStatusException) ex)
                .extracting(ResponseStatusException::getStatusCode)
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getCurrentWeather_shouldThrow500_whenJsonParseFailed() {
        // given:
        Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(Mockito.any(Function.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        Mockito.when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(
                new JsonParseException("error")));

        // when:
        Throwable actual = Assertions.catchThrowable(() -> weatherService.getCurrentWeather(12.3, 45.6));

        // then:
        Assertions.assertThat(actual)
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> (ResponseStatusException) ex)
                .extracting(ResponseStatusException::getStatusCode)
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getCurrentWeather_shouldThrow500_whenJsonIsNull() {
        // given:
        Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(Mockito.any(Function.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        Mockito.when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.empty());

        // when:
        Throwable actual = Assertions.catchThrowable(() -> weatherService.getCurrentWeather(12.3, 45.6));

        // then:
        Assertions.assertThat(actual)
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> (ResponseStatusException) ex)
                .extracting(ResponseStatusException::getStatusCode)
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @SuppressWarnings("unchecked")
    @Test
    void getCurrentAstronomy_shouldReturnJson() throws JsonProcessingException {
        // given:
        Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(Mockito.any(Function.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        String json = """
                {"astronomy": {"astro": {"sunrise": "5:00 AM", "sunset": "6:00 PM", "moonrise": "6:30 AM", "moonset": "7:00 PM", "moon_phase": "New Moon", "moon_illumination": 75}}}
                """;
        Mockito.when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(json));

        // when:
        AstronomyDto actual = weatherService.getCurrentAstronomy(12.3, 45.6);

        // then:
        Assertions.assertThat(actual).isNotNull();
//        Assertions.assertThat(actual.sunrise()).isEqualTo("5:00AM");
    }

    @SuppressWarnings("unchecked")
    @Test
    void getCurrentAstronomy_shouldThrow500_whenExternalServerReturnErrorResponse() {
        // given:
        Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(Mockito.any(Function.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        Mockito.when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(
                new ResponseStatusException(HttpStatus.valueOf(500), "error")));

        // when:
        Throwable actual = Assertions.catchThrowable(() -> weatherService.getCurrentAstronomy(12.3, 45.6));

        // then:
        Assertions.assertThat(actual)
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> (ResponseStatusException) ex)
                .extracting(ResponseStatusException::getStatusCode)
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getCurrentAstronomy_shouldThrow500_whenJsonParseFailed() {
        // given:
        Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(Mockito.any(Function.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        Mockito.when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(
                new JsonParseException("error")));

        // when:
        Throwable actual = Assertions.catchThrowable(() -> weatherService.getCurrentAstronomy(12.3, 45.6));

        // then:
        Assertions.assertThat(actual)
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> (ResponseStatusException) ex)
                .extracting(ResponseStatusException::getStatusCode)
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getCurrentAstronomy_shouldThrow500_whenJsonIsNull() {
        // given:
        Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(Mockito.any(Function.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        Mockito.when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.empty());

        // when:
        Throwable actual = Assertions.catchThrowable(() -> weatherService.getCurrentAstronomy(12.3, 45.6));

        // then:
        Assertions.assertThat(actual)
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> (ResponseStatusException) ex)
                .extracting(ResponseStatusException::getStatusCode)
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}