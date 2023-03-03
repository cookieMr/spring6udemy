package mr.cookie.spring6udemy.units.services;

import mr.cookie.spring6udemy.model.entities.BookEntity;
import mr.cookie.spring6udemy.model.mappers.BookMapper;
import mr.cookie.spring6udemy.model.mappers.BookMapperImpl;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.repositories.BookRepository;
import mr.cookie.spring6udemy.services.BookServiceImpl;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
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

    @Spy
    @NotNull
    private BookMapper bookMapper = new BookMapperImpl();

    @Mock
    @NotNull
    private BookRepository bookRepository;

    @NotNull
    @InjectMocks
    private BookServiceImpl bookService;

    @Captor
    private ArgumentCaptor<BookEntity> bookDtoArgumentCaptor;

    @Test
    void shouldReturnAllBooks() {
        var bookEntity = BOOK_ENTITY_SUPPLIER.get();

        when(this.bookRepository.findAll())
                .thenReturn(Collections.singletonList(bookEntity));

        var result = this.bookService.findAll();

        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .contains(BookDto.builder().id(BOOK_ID).build());

        verify(this.bookRepository).findAll();
        verify(this.bookMapper).mapToModel(anyIterable());
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
        when(this.bookRepository.findById(any(UUID.class))).thenReturn(Optional.of(BOOK_ENTITY_SUPPLIER.get()));

        this.bookService.deleteById(BOOK_ID);

        verify(this.bookRepository).deleteById(BOOK_ID);
        verifyNoMoreInteractions(this.bookRepository);
        verifyNoInteractions(this.bookMapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteBookById() {
        when(this.bookRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.bookService.deleteById(BOOK_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, BookDto.class.getSimpleName(), BOOK_ID);

        verify(this.bookRepository).findById(BOOK_ID);
        verifyNoMoreInteractions(this.bookRepository);
        verifyNoInteractions(this.bookMapper);
    }

}
