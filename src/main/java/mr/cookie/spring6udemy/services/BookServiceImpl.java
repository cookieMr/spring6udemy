package mr.cookie.spring6udemy.services;

import java.util.Optional;
import java.util.UUID;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.model.mappers.BookMapper;
import mr.cookie.spring6udemy.repositories.BookRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookServiceImpl implements BookService {

    private final int defaultPageSize;

    @NotNull
    private final BookMapper bookMapper;

    @NotNull
    private final BookRepository bookRepository;

    public BookServiceImpl(
            @Value("${app.pagination.default-page-size:25}") int defaultPageSize,
            @NotNull BookMapper bookMapper,
            @NotNull BookRepository bookRepository
    ) {
        this.defaultPageSize = validateDefaultPageSize(defaultPageSize);
        this.bookMapper = bookMapper;
        this.bookRepository = bookRepository;
    }

    @NotNull
    @Override
    @Transactional(readOnly = true)
    public Page<BookDto> findAll(@Nullable Integer pageNumber, @Nullable Integer pageSize) {
        var pageRequest = createPageRequest(pageNumber, pageSize, defaultPageSize);
        return bookRepository.findAll(pageRequest)
                .map(bookMapper::map);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookDto> findById(@NotNull UUID id) {
        return bookRepository.findById(id)
                .map(bookMapper::map);
    }

    @NotNull
    @Override
    @Transactional
    public BookDto create(@NotNull BookDto book) {
        return Optional.of(book)
                .map(bookMapper::map)
                .map(bookRepository::save)
                .map(bookMapper::map)
                .orElseThrow();
        // TODO: return conflict when publisher exists
    }

    @NotNull
    @Override
    @Transactional
    public BookDto update(@NotNull UUID id, @NotNull BookDto book) {
        var existingDto = bookRepository.findById(id)
                .orElseThrow(NotFoundEntityException::new);

        existingDto.setTitle(book.getTitle());
        existingDto.setIsbn(book.getIsbn());

        return Optional.of(existingDto)
                .map(bookRepository::save)
                .map(bookMapper::map)
                .orElseThrow();
    }

    @Override
    @Transactional
    public boolean deleteById(@NotNull UUID id) {
        var doesBookExist = bookRepository.existsById(id);

        if (doesBookExist) {
            bookRepository.deleteById(id);
        }

        return doesBookExist;
    }

}
