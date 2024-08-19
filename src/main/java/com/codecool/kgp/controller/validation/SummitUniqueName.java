package com.codecool.kgp.controller.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SummitUniqueNameValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SummitUniqueName {
    String message() default "Ta nazwa jest już zajęta";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
