package pl.manyroutes.controller.validation;

import pl.manyroutes.entity.Challenge;
import pl.manyroutes.errorhandling.DuplicateEntryException;
import pl.manyroutes.repository.ChallengeRepository;
import jakarta.validation.ConstraintValidatorContext;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;


class ChallengeUniqueNameValidatorTest {

    private final ChallengeRepository challengeRepository = Mockito.mock();

    private final ConstraintValidatorContext context = Mockito.mock();

    private final ChallengeUniqueNameValidator validator = new ChallengeUniqueNameValidator(challengeRepository);


    @Test
    void isValid_shouldReturnTrue() {
        //given:
        String name = "name";
        Mockito.when(challengeRepository.findByName(name)).thenReturn(Optional.empty());

        //when:
        boolean actual = validator.isValid(name, context);

        //then:
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    void isValid_shouldThrown() {
        //given:
        String name = "name";
        Challenge challenge = Instancio.create(Challenge.class);
        Mockito.when(challengeRepository.findByName(name)).thenReturn(Optional.of(challenge));

        //when:
        Throwable actual = Assertions.catchThrowable(() -> validator.isValid(name, context));

        //then:
        Assertions.assertThat(actual).isInstanceOf(DuplicateEntryException.class);
    }
}