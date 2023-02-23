package mr.cookie.spring6udemy.repositories;

import org.springframework.data.repository.CrudRepository;

import mr.cookie.spring6udemy.domain.Author;

public interface AuthorRepository extends CrudRepository<Author, Long> {

}
