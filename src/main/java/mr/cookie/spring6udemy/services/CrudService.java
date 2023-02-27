package mr.cookie.spring6udemy.services;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface CrudService<T> {

    @NotNull
    List<T> findAll();

    Optional<T> findById(long id);

    @NotNull
    T create(@NotNull T author);

    @NotNull
    T update(long id, @NotNull T author);

    void deleteById(long id);

}
