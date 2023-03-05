package mr.cookie.spring6udemy.repositories;

import mr.cookie.spring6udemy.model.entities.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookRepository extends JpaRepository<BookEntity, UUID> {

}
