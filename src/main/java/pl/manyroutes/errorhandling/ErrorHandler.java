package pl.manyroutes.errorhandling;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Hidden
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleBadRequest(MethodArgumentNotValidException e) {
        String message = e.getAllErrors().stream()
                .map(ex -> ex.getDefaultMessage())
                .collect(Collectors.joining(" | "));
        return new ErrorResponseDto(message);
    }

    @ExceptionHandler(DuplicateEntryException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleDbConflict(DuplicateEntryException e) {
        return new ErrorResponseDto(e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleUsernameNotFound(UsernameNotFoundException e) {
        return new ErrorResponseDto(e.getMessage());
    }

}
