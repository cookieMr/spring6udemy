package mr.cookie.spring6udemy.exceptions;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CosmereResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @NotNull
    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ErrorDto> handleEntityNotFoundException(@NotNull EntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorDto(exception));
    }

    @NotNull
    @ExceptionHandler({EntityExistsException.class})
    public ResponseEntity<ErrorDto> handleEntityExistsException(@NotNull EntityExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildErrorDto(exception));
    }

    @NotNull
    private static ErrorDto buildErrorDto(@NotNull RuntimeException exception) {
        return ErrorDto.builder()
                .message(exception.getMessage())
                .build();
    }

}
