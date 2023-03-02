package mr.cookie.spring6udemy.services;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CrudService<T> {

    @NotNull
    List<T> findAll();

    Optional<T> findById(@NotNull UUID id);

    @NotNull
    T create(@NotNull T author);

    @NotNull
    T update(@NotNull UUID id, @NotNull T author);

    void deleteById(@NotNull UUID id);

}
