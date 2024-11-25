package com.codecool.kgp.integrationtests;

import com.codecool.kgp.config.WebSignInClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.hamcrest.Matchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/sql/test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestPropertySource(properties = {"spring.datasource.url = jdbc:h2:mem:testdb1;MODE=PostgreSQL;", "server.port=0"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ChallengeIntegrationTest {

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
    void getChallenges_shouldReturnAllChallengesWithoutSummitList_whenNoFieldRequest() {

        // when & then:
        webTestClient.get()
                .uri("/api/v1/challenges/")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$.size()").isEqualTo(4)
                .jsonPath("$[?(@.name=='Conqueror')]").exists()
                .jsonPath("$[*].status").value(everyItem(is("ACTIVE")))
                .jsonPath("$").value(hasItem(hasEntry("id", "8b7935ab-5e22-485b-ae18-7e5ad88b005e")))
                .jsonPath("$").value(not(hasKey("summitsList")));
    }

    @Test
    void getChallenges_shouldReturnAllChallengesWithoutSummitsList_whenNoSummitListFieldRequest() {
        // when & then:
        webTestClient.get()
                .uri("/api/v1/challenges/?fields=id,name")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$.size()").isEqualTo(4)
                .jsonPath("$[?(@.name=='Conqueror')]").exists()
                .jsonPath("$[*].status").value(everyItem(is("ACTIVE")))
                .jsonPath("$").value(hasItem(hasEntry("id", "8b7935ab-5e22-485b-ae18-7e5ad88b005e")))
                .jsonPath("$").value(not(hasKey("summitsList")));
    }

    @Test
    void getChallenges_shouldReturnAllChallengesWithSummitsList_whenSummitListFieldRequest() {
        // when & then:
        webTestClient.get()
                .uri("/api/v1/challenges/?fields=id,name,summitsList")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$.size()").isEqualTo(4)
                .jsonPath("$[?(@.name=='Conqueror')]").exists()
                .jsonPath("$[*].status").value(everyItem(is("ACTIVE")))
                .jsonPath("$").value(hasItem(hasEntry("id", "8b7935ab-5e22-485b-ae18-7e5ad88b005e")))
                .jsonPath("$").value(hasItem(hasKey("summitsList")));
    }
}
