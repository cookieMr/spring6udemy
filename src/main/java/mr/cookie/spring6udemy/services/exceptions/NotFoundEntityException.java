package mr.cookie.spring6udemy.services.exceptions;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class NotFoundEntityException extends RuntimeException {

    /**
     * A template for exception message.
     */
    public static final String ERROR_MESSAGE = "Could not find entity of %s for id %d";

    private final Long id;
    private final Class<?> clazz;

    public NotFoundEntityException(@NotNull Long id, @NotNull Class<?> clazz) {
        super(ERROR_MESSAGE.formatted(clazz.getSimpleName(), id));

        this.id = id;
        this.clazz = clazz;
    }

}
