package com.codecool.kgp.errorhandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

}
