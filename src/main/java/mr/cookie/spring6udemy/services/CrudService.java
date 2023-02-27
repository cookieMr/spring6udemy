package mr.cookie.spring6udemy.services;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CrudService<T> {

    @NotNull
    List<T> findAll();

    @Nullable
    T findById(long id);

    @NotNull
    T create(@NotNull T author);

    @NotNull
    T update(long id, @NotNull T author);

    void deleteById(long id);

}
