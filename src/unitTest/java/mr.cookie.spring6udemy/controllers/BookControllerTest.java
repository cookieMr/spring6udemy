package mr.cookie.spring6udemy.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.services.BookService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private static final UUID BOOK_ID = UUID.randomUUID();
    private static final BookDto BOOK_DTO = BookDto.builder()
            .id(BOOK_ID)
            .build();

    @Mock
    @NotNull
    private BookService bookService;

    @NotNull
    @InjectMocks
    private BookController bookController;

    @Test
    void shoutGetAllBooks() {
        when(bookService.findAll())
                .thenReturn(Stream.of(BOOK_DTO));

        var result = bookController.getAllBooks();

        assertThat(result)
                .isEqualTo(ResponseEntity.ok(List.of(BOOK_DTO)));

        verify(bookService).findAll();
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void shouldGetBookById() {
        when(bookService.findById(BOOK_ID)).thenReturn(Optional.of(BOOK_DTO));

        var result = bookController.getBookById(BOOK_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(BOOK_DTO);

        verify(bookService).findById(BOOK_ID);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindBookById() {
        when(bookService.findById(BOOK_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookController.getBookById(BOOK_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(bookService).findById(BOOK_ID);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void shouldCreateNewBook() {
        when(bookService.create(BOOK_DTO)).thenReturn(BOOK_DTO);

        var result = bookController.createBook(BOOK_DTO);

        assertThat(result)
                .isNotNull()
                .isSameAs(BOOK_DTO);

        verify(bookService).create(BOOK_DTO);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void shouldUpdateExistingBook() {
        when(bookService.update(BOOK_ID, BOOK_DTO)).thenReturn(BOOK_DTO);

        var result = bookController.updateBook(BOOK_ID, BOOK_DTO);

        assertThat(result)
                .isNotNull()
                .isSameAs(BOOK_DTO);

        verify(bookService).update(BOOK_ID, BOOK_DTO);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateBookById() {
        when(bookService.update(BOOK_ID, BOOK_DTO))
                .thenThrow(new NotFoundEntityException(BOOK_ID, BookDto.class));

        assertThatThrownBy(() -> bookController.updateBook(BOOK_ID, BOOK_DTO))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, BookDto.class.getSimpleName(), BOOK_ID);

        verify(bookService).update(BOOK_ID, BOOK_DTO);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void shouldDeleteExistingBook() {
        when(bookService.deleteById(BOOK_ID)).thenReturn(true);

        bookController.deleteBook(BOOK_ID);

        verify(bookService).deleteById(BOOK_ID);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteBookById() {
        when(bookService.deleteById(BOOK_ID)).thenReturn(false);

        assertThatThrownBy(() -> bookController.deleteBook(BOOK_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, BookDto.class.getSimpleName(), BOOK_ID);

        verify(bookService).deleteById(BOOK_ID);
        verifyNoMoreInteractions(bookService);
    }

}