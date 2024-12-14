package pl.manyroutes.entity;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.instancio.Select.field;

class UserChallengeTest {

    @Test
    void assignUserSummit_shouldAddSummit() {
        //given:
        UserChallenge userChallenge = Instancio.of(UserChallenge.class)
                .ignore(field(UserChallenge::getUserSummitsSet))
                .create();
        UserSummit userSummit = Instancio.create(UserSummit.class);

        //when:
        userChallenge.assignUserSummit(userSummit);

        //then:
        Assertions.assertThat(userChallenge).extracting(UserChallenge::getUserSummitsSet, InstanceOfAssertFactories.iterable(UserSummit.class))
                .isNotEmpty()
                .containsExactly(userSummit);
    }

    @Test
    void setFinishedAt_shouldSetDateTime() {
        //given:
        UserChallenge userChallenge = Instancio.of(UserChallenge.class)
                .ignore(field(UserChallenge::getFinishedAt))
                .create();
        LocalDateTime dateTime = LocalDateTime.now();

        //when:
        userChallenge.setFinishedAt(dateTime);

        //then:
        Assertions.assertThat(userChallenge).extracting(UserChallenge::getFinishedAt)
                .isEqualTo(dateTime);
    }

    @Test
    void setFinishedAt_shouldThrownIllegalStateException() {
        //given:
        UserChallenge userChallenge = Instancio.create(UserChallenge.class);
        LocalDateTime dateTime = LocalDateTime.now();

        //when:
        Throwable actual = Assertions.catchThrowable(() -> userChallenge.setFinishedAt(dateTime));

        //then:
        Assertions.assertThat(actual).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("value","cannot", "changed");
    }
}