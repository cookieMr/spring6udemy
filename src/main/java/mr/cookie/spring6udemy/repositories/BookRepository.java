package mr.cookie.spring6udemy.repositories;

import java.util.UUID;
import mr.cookie.spring6udemy.model.entities.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, UUID> {

}
