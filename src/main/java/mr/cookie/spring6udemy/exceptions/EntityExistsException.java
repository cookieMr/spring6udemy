package mr.cookie.spring6udemy.exceptions;

import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import mr.cookie.spring6udemy.model.entities.BookEntity;
import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import org.jetbrains.annotations.NotNull;

public final class EntityExistsException extends RuntimeException {

    public static final String ERROR_MESSAGE = "The same entity of class %s already exists!";

    private EntityExistsException(@NotNull Class<?> clazz) {
        super(ERROR_MESSAGE.formatted(clazz.getSimpleName()));
    }

    public static EntityExistsException ofAuthor() {
        return new EntityExistsException(AuthorEntity.class);
    }

    public static EntityExistsException ofBook() {
        return new EntityExistsException(BookEntity.class);
    }

    public static EntityExistsException ofPublisher() {
        return new EntityExistsException(PublisherEntity.class);
    }

}
