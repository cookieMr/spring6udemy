package mr.cookie.spring6udemy.exceptions;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ControllerAdvice
@NoArgsConstructor
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundEntityException extends RuntimeException {

    /**
     * A template for exception message.
     */
    public static final String ERROR_MESSAGE = "Could not find entity of %s for id %s";

    public NotFoundEntityException(@NotNull UUID id, @NotNull Class<?> clazz) {
        super(ERROR_MESSAGE.formatted(clazz.getSimpleName(), id));
    }

}
