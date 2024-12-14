package pl.manyroutes.validators;

import pl.manyroutes.entity.UserChallenge;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;


class UserChallengeValidatorTest {

    private final UserChallengeValidator userChallengeValidator = new UserChallengeValidator();

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100, 100_000})
    void validateScore_shouldNotThrowAnyExceptions() {
        // given:
        Object type = Instancio.create(UserChallenge.class);
        UUID userChallengeId = UUID.randomUUID();
        Integer score = 100;

        // when:
        Throwable actual = Assertions.catchThrowable(() ->
                userChallengeValidator.validateScore(type, userChallengeId, score));

        // then:
        Assertions.assertThat(actual).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {-100, -1, -100_000})
    void validateScore_shouldNotThrow400(Integer score) {
        // given:
        Object type = Instancio.create(UserChallenge.class);
        UUID userChallengeId = UUID.randomUUID();

        // when:
        Throwable actual = Assertions.catchThrowable(() ->
                userChallengeValidator.validateScore(type, userChallengeId, score));

        // then:
        Assertions.assertThat(actual)
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("score", "ujemne")
                .extracting("status").isEqualTo(HttpStatus.BAD_REQUEST);
    }
}