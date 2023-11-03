package mr.cookie.spring6udemy.controllers;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    @NotNull
    private BookService bookService;

    @NotNull
    @InjectMocks
    private BookController bookController;

    @Test
    void shoutGetAllBooks() {
        var bookDto = BookDto.builder()
                .id(UUID.randomUUID())
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();
        when(bookService.findAll())
                .thenReturn(Stream.of(bookDto));

        var result = bookController.getAllBooks();

        assertThat(result)
                .isEqualTo(ResponseEntity.ok(List.of(bookDto)));

        verify(bookService).findAll();
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void shouldGetBookById() {
        var bookId = UUID.randomUUID();
        var bookDto = BookDto.builder()
                .id(bookId)
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();
        when(bookService.findById(bookId)).thenReturn(Optional.of(bookDto));

        var result = bookController.getBookById(bookId);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.ok(bookDto));

        verify(bookService).findById(bookId);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindBookById() {
        var bookId = UUID.randomUUID();
        when(bookService.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookController.getBookById(bookId))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(bookService).findById(bookId);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void shouldCreateNewBook() {
        var bookDto1st = BookDto.builder()
                .id(UUID.randomUUID())
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();
        var bookDto2nd = BookDto.builder()
                .id(UUID.randomUUID())
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();
        when(bookService.create(bookDto1st)).thenReturn(bookDto2nd);

        var result = bookController.createBook(bookDto1st);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.status(HttpStatus.CREATED).body(bookDto2nd));

        verify(bookService).create(bookDto1st);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void shouldUpdateExistingBook() {
        var bookId = UUID.randomUUID();
        var bookDto1st = BookDto.builder()
                .id(UUID.randomUUID())
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();
        var bookDto2nd = BookDto.builder()
                .id(UUID.randomUUID())
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();
        when(bookService.update(bookId, bookDto1st)).thenReturn(bookDto2nd);

        var result = bookController.updateBook(bookId, bookDto1st);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.ok(bookDto2nd));

        verify(bookService).update(bookId, bookDto1st);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateBookById() {
        var bookId = UUID.randomUUID();
        var bookDto = BookDto.builder()
                .id(bookId)
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();
        when(bookService.update(bookId, bookDto))
                .thenThrow(new NotFoundEntityException(bookId, BookDto.class));

        assertThatThrownBy(() -> bookController.updateBook(bookId, bookDto))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, BookDto.class.getSimpleName(), bookId);

        verify(bookService).update(bookId, bookDto);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void shouldDeleteExistingBook() {
        var bookId = UUID.randomUUID();
        when(bookService.deleteById(bookId)).thenReturn(true);

        var result = bookController.deleteBook(bookId);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.noContent().build());

        verify(bookService).deleteById(bookId);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteBookById() {
        var bookId = UUID.randomUUID();
        when(bookService.deleteById(bookId)).thenReturn(false);

        assertThatThrownBy(() -> bookController.deleteBook(bookId))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, BookDto.class.getSimpleName(), bookId);

        verify(bookService).deleteById(bookId);
        verifyNoMoreInteractions(bookService);
    }

}
