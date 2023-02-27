package mr.cookie.spring6udemy.units.services;

import mr.cookie.spring6udemy.model.entities.BookDto;
import mr.cookie.spring6udemy.model.mappers.BookMapper;
import mr.cookie.spring6udemy.model.mappers.BookMapperImpl;
import mr.cookie.spring6udemy.model.model.Book;
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
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    private static final long BOOK_ID = 1L;
    private static final Book BOOK = Book.builder().id(BOOK_ID).build();
    private static final Supplier<BookDto> BOOK_DTO_SUPPLIER = () -> BookDto.builder().id(BOOK_ID).build();

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
    private ArgumentCaptor<BookDto> bookDtoArgumentCaptor;

    @Test
    void shouldReturnAllBooks() {
        var bookDto = BOOK_DTO_SUPPLIER.get();

        when(this.bookRepository.findAll())
                .thenReturn(Collections.singletonList(bookDto));

        var result = this.bookService.findAll();

        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .contains(Book.builder().id(BOOK_ID).build());

        verify(this.bookRepository).findAll();
        verify(this.bookMapper).mapToModel(anyIterable());
        verify(this.bookMapper).map(bookDto);
        verifyNoMoreInteractions(this.bookRepository, this.bookMapper);
    }

    @Test
    void shouldFindBookById() {
        var bookDto = BOOK_DTO_SUPPLIER.get();

        when(this.bookRepository.findById(anyLong())).thenReturn(Optional.of(bookDto));

        var result = this.bookService.findById(BOOK_ID);

        assertThat(result)
                .isNotNull()
                .returns(BOOK_ID, Book::getId);

        verify(this.bookRepository).findById(BOOK_ID);
        verify(this.bookMapper).map(bookDto);
        verifyNoMoreInteractions(this.bookRepository, this.bookMapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindBookById() {
        when(this.bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.bookService.findById(BOOK_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, Book.class.getSimpleName(), BOOK_ID);

        verify(this.bookRepository).findById(BOOK_ID);
        verifyNoMoreInteractions(this.bookRepository);
        verifyNoInteractions(this.bookMapper);
    }

    @Test
    void shouldCreateNewBook() {
        var bookDto = BOOK_DTO_SUPPLIER.get();

        when(this.bookRepository.save(any(BookDto.class))).thenReturn(bookDto);

        var result = this.bookService.create(BOOK);

        assertThat(result)
                .isNotNull()
                .returns(BOOK_ID, Book::getId);

        verify(this.bookRepository).save(bookDto);
        verify(this.bookMapper).map(bookDto);
        verify(this.bookMapper).map(BOOK);
        verifyNoMoreInteractions(this.bookRepository, this.bookMapper);
    }

    @Test
    void shouldUpdateExistingBook() {
        var bookDto = BOOK_DTO_SUPPLIER.get();
        var updatedBook = Book.builder()
                .title("Elantris")
                .isbn("978-0765350374")
                .build();

        when(this.bookRepository.findById(anyLong())).thenReturn(Optional.of(bookDto));
        when(this.bookRepository.save(any(BookDto.class))).thenReturn(bookDto);

        var result = this.bookService.update(BOOK_ID, updatedBook);

        assertThat(result)
                .isNotNull()
                .returns(BOOK_ID, Book::getId)
                .returns(updatedBook.getTitle(), Book::getTitle)
                .returns(updatedBook.getIsbn(), Book::getIsbn);

        verify(this.bookRepository).findById(BOOK_ID);
        verify(this.bookRepository).save(this.bookDtoArgumentCaptor.capture());
        verify(this.bookMapper).map(bookDto);
        verifyNoMoreInteractions(this.bookRepository, this.bookMapper);

        assertThat(this.bookDtoArgumentCaptor.getValue())
                .isNotNull()
                .returns(BOOK_ID, BookDto::getId)
                .returns(updatedBook.getTitle(), BookDto::getTitle)
                .returns(updatedBook.getIsbn(), BookDto::getIsbn);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateBookById() {
        when(this.bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.bookService.update(BOOK_ID, BOOK))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, Book.class.getSimpleName(), BOOK_ID);

        verify(this.bookRepository).findById(BOOK_ID);
        verifyNoMoreInteractions(this.bookRepository);
        verifyNoInteractions(this.bookMapper);
    }

    @Test
    void shouldDeleteExistingBook() {
        when(this.bookRepository.findById(anyLong())).thenReturn(Optional.of(BOOK_DTO_SUPPLIER.get()));

        this.bookService.deleteById(BOOK_ID);

        verify(this.bookRepository).deleteById(BOOK_ID);
        verifyNoMoreInteractions(this.bookRepository);
        verifyNoInteractions(this.bookMapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteBookById() {
        when(this.bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.bookService.deleteById(BOOK_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, Book.class.getSimpleName(), BOOK_ID);

        verify(this.bookRepository).findById(BOOK_ID);
        verifyNoMoreInteractions(this.bookRepository);
        verifyNoInteractions(this.bookMapper);
    }

}
