package mr.cookie.spring6udemy.repositories;

import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface AuthorRepository extends CrudRepository<AuthorEntity, UUID> {

}
