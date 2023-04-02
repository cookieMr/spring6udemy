package mr.cookie.spring6udemy.services;

import mr.cookie.spring6udemy.model.mappers.BookMapper;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.repositories.BookRepository;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

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
    public Page<BookDto> findAll(@Nullable Integer pageNumber, @Nullable Integer pageSize) {
        var pageRequest = createPageRequest(pageNumber, pageSize, this.defaultPageSize);
        return this.bookRepository.findAll(pageRequest)
                .map(this.bookMapper::map);
    }

    @Override
    public Optional<BookDto> findById(@NotNull UUID id) {
        return this.bookRepository.findById(id)
                .map(this.bookMapper::map);
    }

    @NotNull
    @Override
    @Transactional
    public BookDto create(@NotNull BookDto book) {
        return Optional.of(book)
                .map(this.bookMapper::map)
                .map(this.bookRepository::save)
                .map(this.bookMapper::map)
                .orElseThrow();
        // TODO: return conflict when publisher exists
    }

    @NotNull
    @Override
    @Transactional
    public BookDto update(@NotNull UUID id, @NotNull BookDto book) {
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
    @Transactional
    public boolean deleteById(@NotNull UUID id) {
        var doesBookExist = this.bookRepository.existsById(id);

        if (doesBookExist) {
            this.bookRepository.deleteById(id);
        }

        return doesBookExist;
    }

}
