package com.codecool.kgp.controller.validation;

import com.codecool.kgp.errorhandling.DuplicateEntryException;
import com.codecool.kgp.repository.SummitRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
