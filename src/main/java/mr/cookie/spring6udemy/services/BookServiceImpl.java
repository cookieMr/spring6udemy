package mr.cookie.spring6udemy.services;

import lombok.RequiredArgsConstructor;
import mr.cookie.spring6udemy.exceptions.EntityExistsException;
import mr.cookie.spring6udemy.exceptions.EntityNotFoundException;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.model.mappers.BookMapper;
import mr.cookie.spring6udemy.repositories.BookRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    @NotNull
    private final BookMapper bookMapper;

    @NotNull
    private final BookRepository bookRepository;

    @NotNull
    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto findById(@NotNull UUID id) {
        return bookRepository.findById(id)
                .map(bookMapper::map)
                .orElseThrow(() -> EntityNotFoundException.ofBook(id));
    }

    @NotNull
    @Override
    @Transactional
    public BookDto create(@NotNull BookDto book) {
        var doesBookExist = bookRepository.findByIsbn(book.getIsbn())
                .isPresent();
        if (doesBookExist) {
            throw EntityExistsException.ofBook();
        }

        return Optional.of(book)
                .map(bookMapper::map)
                .map(bookRepository::save)
                .map(bookMapper::map)
                .orElseThrow();
    }

    @NotNull
    @Override
    @Transactional
    public BookDto update(@NotNull UUID id, @NotNull BookDto book) {
        var existingDto = bookRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.ofBook(id));

        if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw EntityExistsException.ofBook();
        }

        existingDto.setTitle(book.getTitle())
                .setIsbn(book.getIsbn());
        return Optional.of(existingDto)
                .map(bookRepository::save)
                .map(bookMapper::map)
                .orElseThrow();
    }

    @Override
    @Transactional
    public void deleteById(@NotNull UUID id) {
        bookRepository.deleteById(id);
    }

}
