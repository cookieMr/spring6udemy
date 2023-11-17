package mr.cookie.spring6udemy.services;

import java.util.UUID;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

public interface CrudService<T> {

    @NotNull
    @Transactional(readOnly = true)
    Stream<T> findAll();

    @Transactional(readOnly = true)
    T findById(@NotNull UUID id);

    @NotNull
    @Transactional
    T create(@NotNull T author);

    @NotNull
    @Transactional
    T update(@NotNull UUID id, @NotNull T author);

    @Transactional
    void deleteById(@NotNull UUID id);

}
