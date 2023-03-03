package mr.cookie.spring6udemy.repositories;

import mr.cookie.spring6udemy.model.entities.BookEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface BookRepository extends CrudRepository<BookEntity, UUID> {

}
