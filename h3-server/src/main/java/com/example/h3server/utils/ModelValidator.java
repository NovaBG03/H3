package com.example.h3server.utils;

import com.example.h3server.exception.CustomException;
import com.example.h3server.models.User;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ModelValidator {

    private static final Validator validator = Validation
            .buildDefaultValidatorFactory()
            .getValidator();

    public static <T> void validate(T obj) {
        List<String> errorMessages = getErrorMessages(obj);

        if (errorMessages.size() > 0) {
            errorMessages.sort(String::compareTo);

            errorMessages.forEach(errorMessage -> {
                throw new CustomException(errorMessage, HttpStatus.BAD_REQUEST);
            });
        }
    }

    private static <T> List<String> getErrorMessages(T obj) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);

        List<String> errorMessages = constraintViolations
                .stream()
                .map(constraintViolation -> constraintViolation.getMessage())
                .collect(Collectors.toList());

        return errorMessages;
    }
}
