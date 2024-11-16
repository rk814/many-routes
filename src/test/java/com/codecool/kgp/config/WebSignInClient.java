package com.codecool.kgp.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class WebSignInClient {

    private final int port;

    @Autowired
    public WebSignInClient(ServletWebServerApplicationContext webServerAppContext) {
        this.port = webServerAppContext.getWebServer().getPort();
    }


    public String signIn(String login, String password) {
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Mono<JsonNode> tokenMono = webClient.post()
                .uri("/api/v1/auth/login")
                .header("Content-Type", "application/json")
                .bodyValue("{\"login\": \"" + login + "\", \"password\": \"" + password + "\"}")
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(JsonNode.class);
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                });
        return Objects.requireNonNull(tokenMono.block()).get("token").asText();
    }
}
