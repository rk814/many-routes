package pl.manyroutes.validators;

import pl.manyroutes.entity.Summit;
import pl.manyroutes.errorhandling.DuplicateEntryException;
import pl.manyroutes.repository.SummitRepository;
import jakarta.validation.ConstraintValidatorContext;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.manyroutes.validators.SummitUniqueNameValidator;

import java.util.Optional;


class SummitUniqueNameValidatorTest {

    private final SummitRepository summitRepository = Mockito.mock();

    private final ConstraintValidatorContext context = Mockito.mock();

    private final SummitUniqueNameValidator validator = new SummitUniqueNameValidator(summitRepository);


    @Test
    void isValid_shouldReturnTrue() {
        //given:
        String name = "name";
        Mockito.when(summitRepository.findByName(name)).thenReturn(Optional.empty());

        //when:
        boolean actual = validator.isValid(name, context);

        //then:
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    void isValid_shouldThrown() {
        //given:
        String name = "name";
        Summit summit = Instancio.create(Summit.class);
        Mockito.when(summitRepository.findByName(name)).thenReturn(Optional.of(summit));

        //when:
        Throwable actual = Assertions.catchThrowable(() -> validator.isValid(name, context));

        //then:
        Assertions.assertThat(actual).isInstanceOf(DuplicateEntryException.class);
    }
}