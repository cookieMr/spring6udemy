package mr.cookie.spring6udemy.repositories;

import java.util.Optional;
import java.util.UUID;
import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<AuthorEntity, UUID> {

    Optional<AuthorEntity> findByFirstNameAndLastName(String firstName, String lastName);

}
