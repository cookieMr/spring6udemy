package mr.cookie.spring6udemy.units.controllers;

import mr.cookie.spring6udemy.controllers.BookController;
import mr.cookie.spring6udemy.model.model.Book;
import mr.cookie.spring6udemy.services.BookService;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

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
    private static final Book BOOK = Book.builder().id(BOOK_ID).build();

    @Mock
    @NotNull
    private BookService bookService;

    @NotNull
    @InjectMocks
    private BookController bookController;

    @Test
    void shoutGetAllBooks() {
        when(this.bookService.findAll()).thenReturn(Collections.singletonList(BOOK));

        var result = this.bookController.getAllBooks();

        assertThat(result)
                .isNotNull()
                .containsOnly(BOOK);

        verify(this.bookService).findAll();
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldGetBookById() {
        when(this.bookService.findById(anyLong())).thenReturn(BOOK);

        var result = this.bookController.getBookById(BOOK_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(BOOK);

        verify(this.bookService).findById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindBookById() {
        when(this.bookService.findById(anyLong()))
                .thenThrow(new NotFoundEntityException(BOOK_ID, Book.class));

        assertThatThrownBy(() -> this.bookController.getBookById(BOOK_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, Book.class.getSimpleName(), BOOK_ID);

        verify(this.bookService).findById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldCreateNewBook() {
        when(this.bookService.create(any(Book.class))).thenReturn(BOOK);

        var result = this.bookController.createBook(BOOK);

        assertThat(result)
                .isNotNull()
                .isSameAs(BOOK);

        verify(this.bookService).create(BOOK);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldUpdateExistingBook() {
        when(this.bookService.update(anyLong(), any(Book.class))).thenReturn(BOOK);

        var result = this.bookController.updateBook(BOOK_ID, BOOK);

        assertThat(result)
                .isNotNull()
                .isSameAs(BOOK);

        verify(this.bookService).update(BOOK_ID, BOOK);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateBookById() {
        when(this.bookService.update(anyLong(), any(Book.class)))
                .thenThrow(new NotFoundEntityException(BOOK_ID, Book.class));

        assertThatThrownBy(() -> this.bookController.updateBook(BOOK_ID, BOOK))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, Book.class.getSimpleName(), BOOK_ID);

        verify(this.bookService).update(BOOK_ID, BOOK);
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
        doThrow(new NotFoundEntityException(BOOK_ID, Book.class))
                .when(this.bookService).deleteById(anyLong());

        assertThatThrownBy(() -> this.bookController.deleteBook(BOOK_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, Book.class.getSimpleName(), BOOK_ID);

        verify(this.bookService).deleteById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

}
