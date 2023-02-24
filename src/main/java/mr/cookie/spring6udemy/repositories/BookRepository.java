package mr.cookie.spring6udemy.repositories;

import org.springframework.data.repository.CrudRepository;

import mr.cookie.spring6udemy.domain.Book;

public interface BookRepository extends CrudRepository<Book, Long> {

}
