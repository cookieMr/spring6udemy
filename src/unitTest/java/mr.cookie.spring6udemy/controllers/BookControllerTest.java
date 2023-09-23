package mr.cookie.spring6udemy.controllers;

import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.services.BookService;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
        var pageBook = new PageImpl<>(List.of(BOOK_DTO));

        when(this.bookService.findAll(anyInt(), anyInt()))
                .thenReturn(pageBook);

        var result = this.bookController.getAllBooks(3, 4);

        assertThat(result)
                .isSameAs(pageBook);

        verify(this.bookService).findAll(3, 4);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldGetBookById() {
        when(this.bookService.findById(any(UUID.class))).thenReturn(Optional.of(BOOK_DTO));

        var result = this.bookController.getBookById(BOOK_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(BOOK_DTO);

        verify(this.bookService).findById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindBookById() {
        when(this.bookService.findById(any(UUID.class))).thenReturn(Optional.empty());

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
        when(this.bookService.update(any(UUID.class), any(BookDto.class))).thenReturn(BOOK_DTO);

        var result = this.bookController.updateBook(BOOK_ID, BOOK_DTO);

        assertThat(result)
                .isNotNull()
                .isSameAs(BOOK_DTO);

        verify(this.bookService).update(BOOK_ID, BOOK_DTO);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateBookById() {
        when(this.bookService.update(any(UUID.class), any(BookDto.class)))
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
        when(this.bookService.deleteById(any(UUID.class))).thenReturn(true);

        this.bookController.deleteBook(BOOK_ID);

        verify(this.bookService).deleteById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteBookById() {
        when(this.bookService.deleteById(any(UUID.class))).thenReturn(false);

        assertThatThrownBy(() -> this.bookController.deleteBook(BOOK_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, BookDto.class.getSimpleName(), BOOK_ID);

        verify(this.bookService).deleteById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

}
