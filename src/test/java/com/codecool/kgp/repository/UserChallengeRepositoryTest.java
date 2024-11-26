package com.codecool.kgp.repository;

import com.codecool.kgp.entity.UserChallenge;
import com.codecool.kgp.entity.UserSummit;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
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
    private UserChallengeRepository testedRepository;

    @Test
    void shouldReadAllChallengesByUserId() {
        //given:
        UUID userId = UUID.fromString("5c39c496-ff63-4c8a-bad4-47d6a97053e7");

        //when:
        List<UserChallenge> actual = testedRepository
                .findAllByUserId(userId);

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

    @Test
    void findAllByUserIdWithAllRelationships_shouldReadAllUserChallengesWithRelationships() {
        //given:
        UUID userId = UUID.fromString("5c39c496-ff63-4c8a-bad4-47d6a97053e7");

        //when:
        List<UserChallenge> actual = testedRepository.findAllByUserIdWithAllRelationships(userId);

        //then:
        Assertions.assertThat(actual).isNotEmpty();
        Assertions.assertThat(actual)
                .filteredOn(uch -> uch.getId().equals(UUID.fromString("cfe64228-2975-41af-bf4c-04bf48bc4523")))
                .first()
                .satisfies(uch -> {
                    Assertions.assertThat(uch.getScore()).isEqualTo(30);
                    Assertions.assertThat(uch.getChallenge().getId()).isEqualTo(UUID.fromString("4c39c496-ff63-4c8a-bad4-47d6a97053e7"));
                    Assertions.assertThat(uch.getUserSummitsList()).isNotEmpty();
                })
                .extracting(UserChallenge::getUserSummitsList, InstanceOfAssertFactories.list(UserSummit.class))
                .extracting(us->us.getSummit().getName())
                .contains("summit1", "summit2");
    }
}