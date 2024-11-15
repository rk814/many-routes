package com.codecool.kgp.integrationtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/sql/test.sql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    private String token;

    
    @BeforeEach
    void setUp() {
        this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

        WebClient webClient = WebClient.create("http://localhost:" + port);
        Mono<String> tokenMono = webClient.post()
                .uri("/api/v1/auth/login")
                .header("Content-Type", "application/json")
                .bodyValue("{\"login\": \"adam_wanderlust\", \"password\": \"safe-password123\"}")
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(String.class);
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                });
        this.token = tokenMono.block();
    }

    @Test
    void getUsers_shouldGetUsers() {
        System.out.println("Token: " + token);

        //when & then:
        webTestClient.get()
                .uri("/api/v1/users/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$[0].login").isEqualTo("adam_wanderlust")
                .jsonPath("$[0].name").isEqualTo("Adam")
                .jsonPath("$[0].email").isEqualTo("adam@adventureworld.com")
                .jsonPath("$[0].role").isEqualTo("ADMIN");
    }
}
