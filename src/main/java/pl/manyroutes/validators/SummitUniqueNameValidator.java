package pl.manyroutes.validators;

import pl.manyroutes.errorhandling.DuplicateEntryException;
import pl.manyroutes.repository.SummitRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SummitUniqueNameValidator implements ConstraintValidator<SummitUniqueName, String> {


    private final SummitRepository summitRepository;
    private String  defaultMessage;

    public SummitUniqueNameValidator(SummitRepository summitRepository) {
        this.summitRepository = summitRepository;
    }

    @Override
    public void initialize(SummitUniqueName constraintAnnotation) {
        this.defaultMessage = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        summitRepository.findByName(name).ifPresent(d -> {
            log.warn("Request for new summit was cancelled due to the name not being unique");
            throw new DuplicateEntryException(defaultMessage);
        });

        return true;
    }
}
