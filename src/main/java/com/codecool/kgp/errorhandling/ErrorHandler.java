package com.codecool.kgp.errorhandling;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleBadRequest(MethodArgumentNotValidException e) {
        String message = e.getAllErrors().stream()
                .map(ex->ex.getDefaultMessage())
                .collect(Collectors.joining(" | "));
        return new ErrorResponseDto(message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleDbConflict(DataIntegrityViolationException e) {
        String message = "Podany login lub e-mail istnieją już w naszym serwisie.";
        return new ErrorResponseDto(message);
    }

}
