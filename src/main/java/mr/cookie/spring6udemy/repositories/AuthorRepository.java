package mr.cookie.spring6udemy.repositories;

import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<AuthorEntity, UUID> {

    Optional<AuthorEntity> findByFirstNameAndLastName(String firstName, String lastName);

}
