package pl.manyroutes.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@Component
public class UserChallengeValidator {

    public <T> void validateScore(T classType, UUID userChallengeId, Integer score) {
        String className = classType.getClass().getSimpleName();
        if (score < 0) {
            log.warn("Request to update score of {} with id '{} had wrong value of '{}'. Score must be positive value.", className, userChallengeId, score);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Punkty (score) nie mogą być ujemne");
        }
    }
}
