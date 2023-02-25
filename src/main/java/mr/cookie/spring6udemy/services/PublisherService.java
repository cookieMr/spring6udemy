package mr.cookie.spring6udemy.services;

import mr.cookie.spring6udemy.model.model.Publisher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PublisherService {

    @NotNull
    List<Publisher> findAll();

    @Nullable
    Publisher findById(@NotNull Long id);

    @NotNull
    Publisher create(@NotNull Publisher publisher);

}
