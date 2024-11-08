package com.codecool.kgp.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import static com.codecool.kgp.config.SpringSecurityConfig.ADMIN;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = "/sql/test.sql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ChallengeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = ADMIN)
    void challenge_shouldReturnStatus200_whenRequestAllChallenges() throws Exception {
        // when:
        ResultActions response = mockMvc.perform(get("/api/v1/challenges/"));

        // then:
        response.andExpect(status().isOk());
        String responseBody = response.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);
    }
}
