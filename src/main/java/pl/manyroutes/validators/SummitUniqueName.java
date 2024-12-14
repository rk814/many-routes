package pl.manyroutes.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SummitUniqueNameValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SummitUniqueName {
    String message() default "Miejsce o tej nazwie już istnieje";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
