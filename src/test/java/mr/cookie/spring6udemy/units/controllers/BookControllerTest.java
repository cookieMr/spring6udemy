package mr.cookie.spring6udemy.units.controllers;

import mr.cookie.spring6udemy.controllers.BookController;
import mr.cookie.spring6udemy.model.model.BookDto;
import mr.cookie.spring6udemy.services.BookService;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private static final long BOOK_ID = 3L;
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
        when(this.bookService.findAll()).thenReturn(Collections.singletonList(BOOK_DTO));

        var result = this.bookController.getAllBooks();

        assertThat(result)
                .isNotNull()
                .containsOnly(BOOK_DTO);

        verify(this.bookService).findAll();
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldGetBookById() {
        when(this.bookService.findById(anyLong())).thenReturn(Optional.of(BOOK_DTO));

        var result = this.bookController.getBookById(BOOK_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(BOOK_DTO);

        verify(this.bookService).findById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindBookById() {
        when(this.bookService.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.bookController.getBookById(BOOK_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(this.bookService).findById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldCreateNewBook() {
        when(this.bookService.create(any(BookDto.class))).thenReturn(BOOK_DTO);

        var result = this.bookController.createBook(BOOK_DTO);

        assertThat(result)
                .isNotNull()
                .isSameAs(BOOK_DTO);

        verify(this.bookService).create(BOOK_DTO);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldUpdateExistingBook() {
        when(this.bookService.update(anyLong(), any(BookDto.class))).thenReturn(BOOK_DTO);

        var result = this.bookController.updateBook(BOOK_ID, BOOK_DTO);

        assertThat(result)
                .isNotNull()
                .isSameAs(BOOK_DTO);

        verify(this.bookService).update(BOOK_ID, BOOK_DTO);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateBookById() {
        when(this.bookService.update(anyLong(), any(BookDto.class)))
                .thenThrow(new NotFoundEntityException(BOOK_ID, BookDto.class));

        assertThatThrownBy(() -> this.bookController.updateBook(BOOK_ID, BOOK_DTO))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, BookDto.class.getSimpleName(), BOOK_ID);

        verify(this.bookService).update(BOOK_ID, BOOK_DTO);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldDeleteExistingBook() {
        this.bookController.deleteBook(BOOK_ID);

        verify(this.bookService).deleteById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteBookById() {
        doThrow(new NotFoundEntityException(BOOK_ID, BookDto.class))
                .when(this.bookService).deleteById(anyLong());

        assertThatThrownBy(() -> this.bookController.deleteBook(BOOK_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, BookDto.class.getSimpleName(), BOOK_ID);

        verify(this.bookService).deleteById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

}
