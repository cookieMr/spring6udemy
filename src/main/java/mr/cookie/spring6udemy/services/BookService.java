package mr.cookie.spring6udemy.services;

import mr.cookie.spring6udemy.domain.Book;

import java.util.List;

public interface BookService {

    List<Book> findAll();

}
