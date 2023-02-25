package mr.cookie.spring6udemy.services;

import mr.cookie.spring6udemy.model.model.Author;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface AuthorService {

    @NotNull
    List<Author> findAll();

}
