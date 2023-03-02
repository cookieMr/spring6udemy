package mr.cookie.spring6udemy.repositories;

import mr.cookie.spring6udemy.model.entities.BookEntity;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<BookEntity, Long> {

}
