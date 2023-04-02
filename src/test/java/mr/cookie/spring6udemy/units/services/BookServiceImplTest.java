package mr.cookie.spring6udemy.units.services;

import mr.cookie.spring6udemy.model.entities.BookEntity;
import mr.cookie.spring6udemy.model.mappers.BookMapper;
import mr.cookie.spring6udemy.model.mappers.BookMapperImpl;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.repositories.BookRepository;
import mr.cookie.spring6udemy.services.BookServiceImpl;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    private static final UUID BOOK_ID = UUID.randomUUID();
    private static final BookDto BOOK = BookDto.builder()
            .id(BOOK_ID)
            .build();
    private static final Supplier<BookEntity> BOOK_ENTITY_SUPPLIER = () -> BookEntity
            .builder()
            .id(BOOK_ID)
            .build();

    @Captor
    private ArgumentCaptor<BookEntity> bookDtoArgumentCaptor;

    private BookMapper bookMapper;
    private BookRepository bookRepository;
    private BookServiceImpl bookService;

    @BeforeEach
    void setupBeforeAll() {
        this.bookRepository = mock(BookRepository.class);
        this.bookMapper = spy(new BookMapperImpl());
        this.bookService = new BookServiceImpl(
                25,
                this.bookMapper,
                this.bookRepository
        );
    }

    @AfterEach
    void cleanUp() {
        reset(this.bookRepository, this.bookMapper);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -42})
    void shouldFailInitializationWithInvalidPageSize(int invalidPageSize) {
        assertThatThrownBy(() -> new BookServiceImpl(invalidPageSize, this.bookMapper, this.bookRepository))
                .isNotNull()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The default pageSize should be greater than zero.");
    }

    @Test
    void shouldReturnAllBooks() {
        var bookEntity = BOOK_ENTITY_SUPPLIER.get();
        var bookPage = new PageImpl<>(List.of(bookEntity));

        when(this.bookRepository.findAll(any(Pageable.class)))
                .thenReturn(bookPage);

        var result = this.bookService.findAll(null, null);

        assertThat(result)
                .isNotNull()
                .hasSize(1);

        verify(this.bookRepository).findAll(any(PageRequest.class));
        verify(this.bookMapper).map(bookEntity);
        verifyNoMoreInteractions(this.bookRepository, this.bookMapper);
    }

    @Test
    void shouldFindBookById() {
        var bookEntity = BOOK_ENTITY_SUPPLIER.get();

        when(this.bookRepository.findById(any(UUID.class))).thenReturn(Optional.of(bookEntity));

        var result = this.bookService.findById(BOOK_ID);

        assertThat(result)
                .isNotNull()
                .isPresent()
                .get()
                .returns(BOOK_ID, BookDto::getId);

        verify(this.bookRepository).findById(BOOK_ID);
        verify(this.bookMapper).map(bookEntity);
        verifyNoMoreInteractions(this.bookRepository, this.bookMapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindBookById() {
        when(this.bookRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        var result = this.bookService.findById(BOOK_ID);

        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(this.bookRepository).findById(BOOK_ID);
        verifyNoMoreInteractions(this.bookRepository);
        verifyNoInteractions(this.bookMapper);
    }

    @Test
    void shouldCreateNewBook() {
        var bookEntity = BOOK_ENTITY_SUPPLIER.get();

        when(this.bookRepository.save(any(BookEntity.class))).thenReturn(bookEntity);

        var result = this.bookService.create(BOOK);

        assertThat(result)
                .isNotNull()
                .returns(BOOK_ID, BookDto::getId);

        verify(this.bookRepository).save(bookEntity);
        verify(this.bookMapper).map(bookEntity);
        verify(this.bookMapper).map(BOOK);
        verifyNoMoreInteractions(this.bookRepository, this.bookMapper);
    }

    @Test
    void shouldUpdateExistingBook() {
        var bookEntity = BOOK_ENTITY_SUPPLIER.get();
        var updatedBookDto = BookDto.builder()
                .title("Elantris")
                .isbn("978-0765350374")
                .build();

        when(this.bookRepository.findById(any(UUID.class))).thenReturn(Optional.of(bookEntity));
        when(this.bookRepository.save(any(BookEntity.class))).thenReturn(bookEntity);

        var result = this.bookService.update(BOOK_ID, updatedBookDto);

        assertThat(result)
                .isNotNull()
                .returns(BOOK_ID, BookDto::getId)
                .returns(updatedBookDto.getTitle(), BookDto::getTitle)
                .returns(updatedBookDto.getIsbn(), BookDto::getIsbn);

        verify(this.bookRepository).findById(BOOK_ID);
        verify(this.bookRepository).save(this.bookDtoArgumentCaptor.capture());
        verify(this.bookMapper).map(bookEntity);
        verifyNoMoreInteractions(this.bookRepository, this.bookMapper);

        assertThat(this.bookDtoArgumentCaptor.getValue())
                .isNotNull()
                .returns(BOOK_ID, BookEntity::getId)
                .returns(updatedBookDto.getTitle(), BookEntity::getTitle)
                .returns(updatedBookDto.getIsbn(), BookEntity::getIsbn);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateBookById() {
        when(this.bookRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.bookService.update(BOOK_ID, BOOK))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(this.bookRepository).findById(BOOK_ID);
        verifyNoMoreInteractions(this.bookRepository);
        verifyNoInteractions(this.bookMapper);
    }

    @Test
    void shouldDeleteExistingBook() {
        when(this.bookRepository.existsById(any(UUID.class))).thenReturn(true);

        var result = this.bookService.deleteById(BOOK_ID);

        assertThat(result).isTrue();

        verify(this.bookRepository).deleteById(BOOK_ID);
        verify(this.bookRepository).existsById(BOOK_ID);
        verifyNoMoreInteractions(this.bookRepository);
        verifyNoInteractions(this.bookMapper);
    }

    @Test
    void shouldNotDeleteNotExistingBook() {
        when(this.bookRepository.existsById(any(UUID.class))).thenReturn(false);

        var result = this.bookService.deleteById(BOOK_ID);

        assertThat(result).isFalse();

        verify(this.bookRepository).existsById(BOOK_ID);
        verifyNoMoreInteractions(this.bookRepository);
        verifyNoInteractions(this.bookMapper);
    }

}
