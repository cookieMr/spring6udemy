package mr.cookie.spring6udemy.units.controllers;

import mr.cookie.spring6udemy.controllers.BookController;
import mr.cookie.spring6udemy.model.model.Book;
import mr.cookie.spring6udemy.services.BookService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @NotNull
    private static Stream<Book> bookStream() {
        return Stream.of(BOOK);
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("bookStream")
    void shouldGetBookById(@Nullable Book book) {
        when(this.bookService.findById(anyLong())).thenReturn(book);

        var result = this.bookController.getBookById(BOOK_ID);

        assertThat(result)
                .isEqualTo(book);

        verify(this.bookService).findById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldCreateNewBook() {
        when(this.bookService.create(any(Book.class))).thenReturn(BOOK);

        var result = this.bookController.createNewBook(BOOK);

        assertThat(result)
                .isNotNull()
                .isSameAs(BOOK);

        verify(this.bookService).create(BOOK);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldUpdateExistingBook() {
        when(this.bookService.update(anyLong(), any(Book.class))).thenReturn(BOOK);

        var result = this.bookController.updateExistingBook(BOOK_ID, BOOK);

        assertThat(result)
                .isNotNull()
                .isSameAs(BOOK);

        verify(this.bookService).update(BOOK_ID, BOOK);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldDeleteExistingBook() {
        this.bookController.deleteBook(BOOK_ID);

        verify(this.bookService).deleteById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

}
