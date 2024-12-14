package pl.manyroutes.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ScoreValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidScore {
    String message() default "Punkty (score) nie mogą być ujemne";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
