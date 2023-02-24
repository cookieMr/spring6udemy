package mr.cookie.spring6udemy.repositories;

import org.springframework.data.repository.CrudRepository;

import mr.cookie.spring6udemy.model.entities.BookDto;

public interface BookRepository extends CrudRepository<BookDto, Long> {

}
