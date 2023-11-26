package mr.cookie.spring6udemy.services;

import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface CrudService<T> {

    @NotNull
    @Transactional(readOnly = true)
    List<T> findAll();

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
