package pl.manyroutes.errorhandling;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;


class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleBadRequest_shouldReturnResponseDto() {
        //given:
        BeanPropertyBindingResult bindingResults = new BeanPropertyBindingResult(null, "object");
        bindingResults.addError(new FieldError("object", "email", "Invalid email"));
        bindingResults.addError(new FieldError("object", "login", "Invalid login"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResults);

        //when:
        ErrorResponseDto actual = errorHandler.handleBadRequest(exception);

        //then:
        Assertions.assertThat(actual).extracting(ErrorResponseDto::message)
                .isEqualTo("Invalid email | Invalid login");
    }

    @Test
    void handleDbConflict_shouldReturnErrorResponseDto() {
        //given:
        DuplicateEntryException exception = new DuplicateEntryException("Duplicates Database Entry!");

        //when:
        ErrorResponseDto actual = errorHandler.handleDbConflict(exception);

        //then:
        Assertions.assertThat(actual).extracting(ErrorResponseDto::message).isEqualTo("Duplicates Database Entry!");
    }

    @Test
    void handleUsernameNotFound_shouldReturnErrorResponseDto() {
        //given:
        UsernameNotFoundException exception = new UsernameNotFoundException("User name not found!");

        //when:
        ErrorResponseDto actual = errorHandler.handleUsernameNotFound(exception);

        //then:
        Assertions.assertThat(actual).extracting(ErrorResponseDto::message).isEqualTo("User name not found!");
    }
}