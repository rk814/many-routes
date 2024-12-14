package pl.manyroutes.repository;

import pl.manyroutes.entity.Summit;
import pl.manyroutes.entity.enums.Status;
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

    @Test
    void findAllByStatusWithChallenges_shouldReadAllSummitsWithChallenges() {
        //given:
        Status status = Status.ACTIVE;

        //when:
        List<Summit> actual = testedRepository.findAllByStatusWithChallenges(status);

        //then:
        Assertions.assertThat(actual).isNotEmpty();
        Assertions.assertThat(actual).extracting(Summit::getName).containsAnyOf("summit1", "summit2");
        Assertions.assertThat(actual)
                .filteredOn(s -> s.getName().equals("summit1"))
                .first().extracting(Summit::getChallengesSet).isNotNull();
        Assertions.assertThat(actual).extracting(Summit::getStatus).containsOnly(Status.ACTIVE);
    }
}