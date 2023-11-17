package mr.cookie.spring6udemy.controllers;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;
import mr.cookie.spring6udemy.exceptions.EntityNotFoundException;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.model.entities.BookEntity;
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
    private BookService service;

    @NotNull
    @InjectMocks
    private BookController controller;

    @Test
    void shoutGetAllBooks() {
        var bookDto = BookDto.builder()
                .id(randomUUID())
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();
        when(service.findAll())
                .thenReturn(Stream.of(bookDto));

        var result = controller.getAllBooks();

        assertThat(result)
                .isEqualTo(ResponseEntity.ok(List.of(bookDto)));

        verify(service).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldGetBookById() {
        var bookId = randomUUID();
        var bookDto = BookDto.builder()
                .id(bookId)
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();
        when(service.findById(bookId)).thenReturn(bookDto);

        var result = controller.getBookById(bookId);

        assertThat(result).isSameAs(bookDto);

        verify(service).findById(bookId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindBookById() {
        var bookId = randomUUID();
        when(service.findById(bookId)).thenThrow(EntityNotFoundException.ofBook(bookId));

        assertThatThrownBy(() -> controller.getBookById(bookId))
                .isNotNull()
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage(EntityNotFoundException.ERROR_MESSAGE, BookEntity.class.getSimpleName(), bookId);

        verify(service).findById(bookId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldCreateNewBook() {
        var bookDto1st = BookDto.builder()
                .id(randomUUID())
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();
        var bookDto2nd = BookDto.builder()
                .id(randomUUID())
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();
        when(service.create(bookDto1st)).thenReturn(bookDto2nd);

        var result = controller.createBook(bookDto1st);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.status(HttpStatus.CREATED).body(bookDto2nd));

        verify(service).create(bookDto1st);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldUpdateExistingBook() {
        var bookId = randomUUID();
        var bookDto1st = BookDto.builder()
                .id(randomUUID())
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();
        var bookDto2nd = BookDto.builder()
                .id(randomUUID())
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();
        when(service.update(bookId, bookDto1st)).thenReturn(bookDto2nd);

        var result = controller.updateBook(bookId, bookDto1st);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.ok(bookDto2nd));

        verify(service).update(bookId, bookDto1st);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateBookById() {
        var bookId = randomUUID();
        var bookDto = BookDto.builder()
                .id(bookId)
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();
        when(service.update(bookId, bookDto))
                .thenThrow(EntityNotFoundException.ofBook(bookId));

        assertThatThrownBy(() -> controller.updateBook(bookId, bookDto))
                .isNotNull()
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage(EntityNotFoundException.ERROR_MESSAGE, BookEntity.class.getSimpleName(), bookId);

        verify(service).update(bookId, bookDto);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldDeleteExistingBook() {
        var bookId = randomUUID();

        controller.deleteBook(bookId);

        verify(service).deleteById(bookId);
        verifyNoMoreInteractions(service);
    }

}
