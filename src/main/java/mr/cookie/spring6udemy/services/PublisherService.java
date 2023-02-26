package mr.cookie.spring6udemy.services;

import mr.cookie.spring6udemy.model.model.Publisher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PublisherService {

    @NotNull
    List<Publisher> findAll();

    @Nullable
    Publisher findById(long id);

    @NotNull
    Publisher create(@NotNull Publisher publisher);

    @NotNull
    Publisher update(long id, @NotNull Publisher publisher);

}
