package mr.cookie.spring6udemy.repositories;

import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import org.springframework.data.repository.CrudRepository;

public interface AuthorRepository extends CrudRepository<AuthorEntity, Long> {

}
