package com.codecool.kgp.repository;

import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.enums.Status;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Sql(value = "/sql/test.sql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SummitRepositoryTest {

    @Autowired
    private SummitRepository testedRepository;

    @Test
    void findByName_shouldReadSummit() {
        //given:
        String name = "summit1";

        //when:
        Optional<Summit> actual = testedRepository.findByName(name);

        //then:
        Assertions.assertThat(actual).isPresent()
                .get().extracting(Summit::getName).isEqualTo(name);
    }

    @Test
    void findByName_shouldReadEmptyOptional() {
        //given:
        String name = "nonExistingSummit";

        //when:
        Optional<Summit> actual = testedRepository.findByName(name);

        //then:
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    void findAllByStatus_shouldReadAllSummits() {
        //given:
        Status status = Status.ACTIVE;
        int expectedSize = 2;

        //when:
        List<Summit> actual = testedRepository.findAllByStatus(status);

        //then:
        Assertions.assertThat(actual.size()).isEqualTo(expectedSize);
        Assertions.assertThat(actual).extracting(Summit::getName).containsExactlyInAnyOrder("summit1", "summit2");
    }

    @Test
    void findAllByStatus_shouldReadEmptyList() {
        //given:
        Status status = Status.REMOVED;
        int expectedSize = 0;

        //when:
        List<Summit> actual = testedRepository.findAllByStatus(status);

        //then:
        Assertions.assertThat(actual.size()).isEqualTo(expectedSize);
        Assertions.assertThat(actual).extracting(Summit::getName).doesNotContain("summit1", "summit2");
    }
}