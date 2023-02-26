package mr.cookie.spring6udemy.services;

import mr.cookie.spring6udemy.model.model.Author;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface AuthorService {

    @NotNull
    List<Author> findAll();

    @Nullable
    Author findById(long id);

    @NotNull
    Author create(@NotNull Author author);

    @NotNull
    Author update(long id, @NotNull Author author);

}
