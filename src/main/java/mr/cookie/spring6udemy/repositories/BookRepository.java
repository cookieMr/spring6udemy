package mr.cookie.spring6udemy.repositories;

import java.util.Optional;
import java.util.UUID;
import mr.cookie.spring6udemy.model.entities.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, UUID> {

    Optional<BookEntity> findByIsbn(String isbn);

}
