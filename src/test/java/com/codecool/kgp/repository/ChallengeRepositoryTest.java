package com.codecool.kgp.repository;

import com.codecool.kgp.entity.Challenge;
import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.enums.Status;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;


@DataJpaTest
@Sql(value = "/sql/test.sql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChallengeRepositoryTest {

    @Autowired
    private ChallengeRepository testedRepository;


    @Test
    void findAllByStatus_shouldReadAllActiveChallengesFromDb() {
        //given:
        Status status = Status.ACTIVE;
        List<String> expectedNames = List.of("test1-challenge", "test2-challenge", "test3-challenge");

        //when:
        List<Challenge> actual = testedRepository.findAllByStatus(status);
        List<String> actualNames = actual.stream().map(Challenge::getName).toList();

        //then:
        Assertions.assertThat(actualNames).containsAll(expectedNames)
                .doesNotContain("test4-challenge");
        Assertions.assertThat(actual).filteredOn(a -> a.getName().equals("test1-challenge"))
                .hasSize(1)
                .first()
                .satisfies(challenge -> Assertions.assertThat(Hibernate.isInitialized(challenge.getSummitsList()))
                        .isFalse());
    }

    @Test
    void findAllByStatus_shouldReadEmptyListFromDb() {
        //given:
        Status status = Status.DEVELOP;
        List<String> noneExpectedNames = List.of("test1-challenge", "test2-challenge", "test3-challenge");

        //when:
        List<Challenge> actual = testedRepository.findAllByStatus(status);
        List<String> actualNames = actual.stream().map(Challenge::getName).toList();

        //then:
        Assertions.assertThat(actual).hasSize(0);
        Assertions.assertThat(actualNames).doesNotContainAnyElementsOf(noneExpectedNames);
    }

    @Test
    void findAllByStatusWithSummits_shouldReadAllActiveChallengesFromDb() {
        //given:
        Status status = Status.ACTIVE;
        List<String> expectedNames = List.of("test1-challenge", "test2-challenge", "test3-challenge");

        //when:
        List<Challenge> actual = testedRepository.findAllByStatusWithSummits(status);
        List<String> actualNames = actual.stream().map(Challenge::getName).toList();

        //then:
        Assertions.assertThat(actualNames).containsAll(expectedNames)
                .doesNotContain("test4-challenge");
        Assertions.assertThat(actual).filteredOn(a -> a.getName().equals("test1-challenge"))
                .hasSize(1)
                .first()
                .satisfies(challenge -> Assertions.assertThat(Hibernate.isInitialized(challenge.getSummitsList()))
                        .isTrue())
                .extracting(Challenge::getSummitsList, InstanceOfAssertFactories.list(Summit.class))
                .hasSize(2);
    }
}