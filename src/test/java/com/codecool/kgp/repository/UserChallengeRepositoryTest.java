package com.codecool.kgp.repository;

import com.codecool.kgp.entity.UserChallenge;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@DataJpaTest
@Sql(value = "/sql/test.sql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserChallengeRepositoryTest {

    @Autowired
    private UserChallengeRepository testedUserChallengeRepository;

    @Test
    void shouldReadAllChallengesByUserId() {
        //when:
        List<UserChallenge> actual = testedUserChallengeRepository
                .findAllByUserId(UUID.fromString("5c39c496-ff63-4c8a-bad4-47d6a97053e7"));

        //then:
        Assertions.assertThat(actual).hasSize(1);
        Assertions.assertThat(actual.get(0).getId()).isEqualTo(UUID.fromString("cfe64228-2975-41af-bf4c-04bf48bc4523"));
        Assertions.assertThat(actual.get(0).getChallenge().getId())
                .isEqualTo(UUID.fromString("4c39c496-ff63-4c8a-bad4-47d6a97053e7"));
        Assertions.assertThat(actual.get(0).getStartedAt()).isEqualTo(LocalDateTime
                .of(2004, 10, 19, 10, 23, 54));
        Assertions.assertThat(actual.get(0).getFinishedAt()).isEqualTo(LocalDateTime
                .of(2014, 3, 11, 8, 23, 54));
        Assertions.assertThat(actual.get(0).getScore()).isEqualTo(30);
    }

}