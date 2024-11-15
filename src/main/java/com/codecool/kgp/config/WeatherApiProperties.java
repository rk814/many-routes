package com.codecool.kgp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "weather.api")
public record WeatherApiProperties(
        String key,
        String url
) {
}
