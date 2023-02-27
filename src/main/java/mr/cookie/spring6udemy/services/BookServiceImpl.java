package mr.cookie.spring6udemy.services;

import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.model.mappers.BookMapper;
import mr.cookie.spring6udemy.model.model.Book;
import mr.cookie.spring6udemy.repositories.BookRepository;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    @NotNull
    private final BookMapper bookMapper;

    @NotNull
    private final BookRepository bookRepository;

    @NotNull
    @Override
    public List<Book> findAll() {
        return Optional.of(this.bookRepository)
                .map(CrudRepository::findAll)
                .map(this.bookMapper::mapToModel)
                .orElse(Collections.emptyList());
    }

    @Override
    public Optional<Book> findById(long id) {
        return this.bookRepository.findById(id)
                .map(this.bookMapper::map);
    }

    @NotNull
    @Override
    public Book create(@NotNull Book book) {
        return Optional.of(book)
                .map(this.bookMapper::map)
                .map(this.bookRepository::save)
                .map(this.bookMapper::map)
                .orElseThrow();
        // TODO: return conflict when publisher exists
    }

    @NotNull
    @Override
    public Book update(long id, @NotNull Book book) {
        var existingDto = this.bookRepository.findById(id)
                .orElseThrow(NotFoundEntityException::new);

        existingDto.setTitle(book.getTitle());
        existingDto.setIsbn(book.getIsbn());

        return Optional.of(existingDto)
                .map(this.bookRepository::save)
                .map(this.bookMapper::map)
                .orElseThrow();
    }

    @Override
    public void deleteById(long id) {
        var doesBookExist = this.bookRepository.findById(id)
                .isPresent();

        if (doesBookExist) {
            this.bookRepository.deleteById(id);
        } else {
            throw new NotFoundEntityException(id, Book.class);
        }
    }

}
