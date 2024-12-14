package pl.manyroutes.validators;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ChallengeUniqueNameValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChallengeUniqueName {
    String message() default "Wyzwanie o tej samej nazwie już istnieje";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
