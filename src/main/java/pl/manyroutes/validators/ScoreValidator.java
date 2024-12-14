package pl.manyroutes.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class ScoreValidator implements ConstraintValidator<ValidScore, Integer> {

    @Override
    public boolean isValid(Integer score, ConstraintValidatorContext context) {

        if (score!=null && score >= 0) {
            return true;
        } else {
            log.warn("Request was cancelled due to the invalid score number");
//            return false; // with false returns 400 Validation failure
            String errorMessage = context.getDefaultConstraintMessageTemplate();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }
}
