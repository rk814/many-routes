package com.codecool.kgp.integrationtests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;


@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = "/sql/test.sql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @WithUserDetails(value = "adam_wanderlust")
    void getUsers_shouldGetUsers() {

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
