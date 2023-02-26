package mr.cookie.spring6udemy.services;

import mr.cookie.spring6udemy.model.model.Book;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface BookService {

    @NotNull
    List<Book> findAll();

    @Nullable
    Book findById(long id);

    @NotNull
    Book create(@NotNull Book book);

    @NotNull
    Book update(long id, @NotNull Book book);

    void deleteById(long id);

}
