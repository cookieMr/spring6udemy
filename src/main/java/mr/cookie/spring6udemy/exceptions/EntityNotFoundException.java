package mr.cookie.spring6udemy.exceptions;

import java.util.UUID;
import lombok.NoArgsConstructor;
import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import mr.cookie.spring6udemy.model.entities.BookEntity;
import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public class EntityNotFoundException extends RuntimeException {

    /**
     * A template for exception message.
     */
    public static final String ERROR_MESSAGE = "Could not find entity of %s for id %s";

    public EntityNotFoundException(@NotNull UUID id, @NotNull Class<?> clazz) {
        super(ERROR_MESSAGE.formatted(clazz.getSimpleName(), id));
    }

    public static EntityNotFoundException ofAuthor(UUID id) {
        return new EntityNotFoundException(id, AuthorEntity.class);
    }

    public static EntityNotFoundException ofBook(UUID id) {
        return new EntityNotFoundException(id, BookEntity.class);
    }

    public static EntityNotFoundException ofPublisher(UUID id) {
        return new EntityNotFoundException(id, PublisherEntity.class);
    }

}