package mr.cookie.spring6udemy.services;

import mr.cookie.spring6udemy.model.model.Book;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface BookService {

    @NotNull
    List<Book> findAll();

}
