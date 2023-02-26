package mr.cookie.spring6udemy.controllers;

import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.services.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionControllerAdvice {

    @ExceptionHandler(NotFoundEntityException.class)
    @NotNull
    public ResponseEntity<String> handleNotFoundEntityException(@NotNull NotFoundEntityException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

}
