package com.codecool.kgp.repository;

import com.codecool.kgp.entity.User;
import com.codecool.kgp.entity.UserChallenge;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;


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
                .satisfies(adam -> Assertions.assertThat(adam.getUserChallenges()).isNotEmpty())
                .extracting(User::getUserChallenges, InstanceOfAssertFactories.list(UserChallenge.class))
                .first()
                .satisfies(uch -> Assertions.assertThat(uch.getChallenge()).isNotNull());
    }
}