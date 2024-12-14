package pl.manyroutes.controller.validation;

import pl.manyroutes.errorhandling.DuplicateEntryException;
import pl.manyroutes.repository.ChallengeRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ChallengeUniqueNameValidator implements ConstraintValidator<ChallengeUniqueName, String> {

    private final ChallengeRepository challengeRepository;

    private String defaultMessage;


    public ChallengeUniqueNameValidator(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }


    @Override
    public void initialize(ChallengeUniqueName constraintAnnotation) {
        this.defaultMessage = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        challengeRepository.findByName(name).ifPresent(ch -> {
            log.warn("Request for new challenge was cancelled due to the name not being unique");
            throw new DuplicateEntryException(defaultMessage);
        });
        return true;
    }
}
