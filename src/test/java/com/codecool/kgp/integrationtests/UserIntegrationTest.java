package com.codecool.kgp.integrationtests;

import com.codecool.kgp.config.WebSignInClient;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/sql/test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserIntegrationTest {

    @LocalServerPort
    private int port;

    private String token;

    @Autowired
    private WebTestClient webTestClient;


    @BeforeAll
    void setUpAll() {
        WebSignInClient webSignInClient = new WebSignInClient(port);
        this.token = webSignInClient.signIn("adam_wanderlust", "safe-password123");
    }

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }


    @Test
    void getUsers_shouldGetAllUsers() {
        //when & then:
        webTestClient.get()
                .uri("/api/v1/users/")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].login").isEqualTo("adam_wanderlust")
                .jsonPath("$[0].name").isEqualTo("Adam")
                .jsonPath("$[0].email").isEqualTo("adam@adventureworld.com")
                .jsonPath("$[0].role").isEqualTo("ADMIN");
    }

    @Test
    void getUserById_shouldReturnUser() {
        //given:
        String id = "c82f1e44-12a2-4e9b-acd9-5b7a019c6d8c";

        //when & then:
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/users/{id}")
                        .build(id))
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.login").isEqualTo("chris_crescendo")
                .jsonPath("$.name").isEqualTo("Chris")
                .jsonPath("$.email").isEqualTo("chris@musicworld.com")
                .jsonPath("$.userChallengesSetSimplified").isNotEmpty()
                .jsonPath("$.userChallengesSetSimplified[*].challengeName").value(Matchers.contains("test2-challenge"));
    }

    @Test
    void getUser_shouldReturnUser() {
        //when & then:
        webTestClient.get()
                .uri("/api/v1/users/me")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.login").isEqualTo("adam_wanderlust")
                .jsonPath("$.name").isEqualTo("Adam")
                .jsonPath("$.email").isEqualTo("adam@adventureworld.com")
                .jsonPath("$.userChallengesSetSimplified").isNotEmpty()
                .jsonPath("$.userChallengesSetSimplified[*].challengeName").value(Matchers.hasItem("Conqueror"));
    }
}
