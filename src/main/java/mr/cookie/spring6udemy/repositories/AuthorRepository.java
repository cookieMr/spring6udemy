package mr.cookie.spring6udemy.repositories;

import org.springframework.data.repository.CrudRepository;

import mr.cookie.spring6udemy.model.entities.AuthorDto;

public interface AuthorRepository extends CrudRepository<AuthorDto, Long> {

}
