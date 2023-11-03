package mr.cookie.spring6udemy.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.model.entities.BookEntity;
import mr.cookie.spring6udemy.model.mappers.BookMapper;
import mr.cookie.spring6udemy.model.mappers.BookMapperImpl;
import mr.cookie.spring6udemy.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    private static final UUID BOOK_ID = UUID.randomUUID();
    private static final BookDto BOOK = BookDto.builder()
            .id(BOOK_ID)
            .build();
    // todo: refactor to not use it
    private static final Supplier<BookEntity> BOOK_ENTITY_SUPPLIER = () -> BookEntity
            .builder()
            .id(BOOK_ID)
            .build();

    @Captor
    private ArgumentCaptor<BookEntity> bookDtoArgumentCaptor;

    @Spy
    private BookMapper bookMapper = new BookMapperImpl();

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void shouldReturnAllBooks() {
        var bookEntity = BOOK_ENTITY_SUPPLIER.get();

        when(bookRepository.findAll())
                .thenReturn(List.of(bookEntity));

        var result = bookService.findAll();

        assertThat(result)
                .isNotNull()
                .hasSize(1);

        verify(bookRepository).findAll();
        verify(bookMapper).map(bookEntity);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void shouldFindBookById() {
        var bookEntity = BOOK_ENTITY_SUPPLIER.get();

        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(bookEntity));

        var result = bookService.findById(BOOK_ID);

        assertThat(result)
                .isNotNull()
                .isPresent()
                .get()
                .returns(BOOK_ID, BookDto::getId);

        verify(bookRepository).findById(BOOK_ID);
        verify(bookMapper).map(bookEntity);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindBookById() {
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.empty());

        var result = bookService.findById(BOOK_ID);

        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(bookRepository).findById(BOOK_ID);
        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(bookMapper);
    }

    @Test
    void shouldCreateNewBook() {
        var bookEntity = BOOK_ENTITY_SUPPLIER.get();

        when(bookRepository.save(bookEntity)).thenReturn(bookEntity);

        var result = bookService.create(BOOK);

        assertThat(result)
                .isNotNull()
                .returns(BOOK_ID, BookDto::getId);

        verify(bookRepository).save(bookEntity);
        verify(bookMapper).map(bookEntity);
        verify(bookMapper).map(BOOK);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void shouldUpdateExistingBook() {
        var bookEntity = BOOK_ENTITY_SUPPLIER.get();
        var updatedBookDto = BookDto.builder()
                .title("Elantris")
                .isbn("978-0765350374")
                .build();

        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(bookEntity));
        when(bookRepository.save(bookEntity)).thenReturn(bookEntity);

        var result = bookService.update(BOOK_ID, updatedBookDto);

        assertThat(result)
                .isNotNull()
                .returns(BOOK_ID, BookDto::getId)
                .returns(updatedBookDto.getTitle(), BookDto::getTitle)
                .returns(updatedBookDto.getIsbn(), BookDto::getIsbn);

        verify(bookRepository).findById(BOOK_ID);
        verify(bookRepository).save(bookDtoArgumentCaptor.capture());
        verify(bookMapper).map(bookEntity);
        verifyNoMoreInteractions(bookRepository, bookMapper);

        assertThat(bookDtoArgumentCaptor.getValue())
                .isNotNull()
                .returns(BOOK_ID, BookEntity::getId)
                .returns(updatedBookDto.getTitle(), BookEntity::getTitle)
                .returns(updatedBookDto.getIsbn(), BookEntity::getIsbn);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateBookById() {
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.update(BOOK_ID, BOOK))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(bookRepository).findById(BOOK_ID);
        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(bookMapper);
    }

    @Test
    void shouldDeleteExistingBook() {
        when(bookRepository.existsById(BOOK_ID)).thenReturn(true);

        var result = bookService.deleteById(BOOK_ID);

        assertThat(result).isTrue();

        verify(bookRepository).deleteById(BOOK_ID);
        verify(bookRepository).existsById(BOOK_ID);
        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(bookMapper);
    }

    @Test
    void shouldNotDeleteNotExistingBook() {
        when(bookRepository.existsById(BOOK_ID)).thenReturn(false);

        var result = bookService.deleteById(BOOK_ID);

        assertThat(result).isFalse();

        verify(bookRepository).existsById(BOOK_ID);
        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(bookMapper);
    }

}
