package pl.manyroutes.repository;

import pl.manyroutes.entity.User;
import pl.manyroutes.entity.UserChallenge;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;


@ActiveProfiles("test")
@DataJpaTest
@Sql(scripts = "/sql/test.sql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository testedRepository;


    @Test
    void findAllWithChallenges() {
        // when
        var actual = testedRepository.findAllWithChallenges();

        // then
        Assertions.assertThat(actual).hasSize(3)
                .filteredOn(user -> user.getName().equals("Adam"))
                .singleElement()
                .satisfies(adam -> Assertions.assertThat(adam.getUserChallengesSet()).isNotEmpty())
                .extracting(User::getUserChallengesSet, InstanceOfAssertFactories.iterable(UserChallenge.class))
                .first()
                .satisfies(uch -> Assertions.assertThat(uch.getChallenge()).isNotNull());
    }
}