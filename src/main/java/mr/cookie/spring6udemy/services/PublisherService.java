package mr.cookie.spring6udemy.services;

import mr.cookie.spring6udemy.model.model.Publisher;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PublisherService {

    @NotNull
    List<Publisher> findAll();

}
