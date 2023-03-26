package mr.cookie.spring6udemy.services;

import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CrudService<T> {

    @NotNull
    List<T> findAll();

    Optional<T> findById(@NotNull UUID id);

    @NotNull
    @Transactional
    T create(@NotNull T author);

    @NotNull
    @Transactional
    T update(@NotNull UUID id, @NotNull T author);

    /**
     * Returns {@code true} if en entity with provided ID was removed, {@code false} otherwise.
     * @param id id of an entity to remove
     * @return {@code true} if an entity was removed, {@code false} otherwise
     */
    @Transactional
    boolean deleteById(@NotNull UUID id);

}
