package mr.cookie.spring6udemy.controllers;

import static java.util.UUID.randomUUID;
import static mr.cookie.spring6udemy.providers.dtos.BookDtoProvider.provideBookDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import mr.cookie.spring6udemy.exceptions.EntityNotFoundException;
import mr.cookie.spring6udemy.model.entities.BookEntity;
import mr.cookie.spring6udemy.services.BookService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
        var bookDto = provideBookDto(randomUUID());

        when(service.findAll())
                .thenReturn(List.of(bookDto));

        var result = controller.getAllBooks();

        assertThat(result)
                .isNotNull()
                .isEqualTo(List.of(bookDto));

        verify(service).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldGetBookById() {
        var bookId = randomUUID();
        var bookDto = provideBookDto(bookId);

        when(service.findById(bookId))
                .thenReturn(bookDto);

        var result = controller.getBookById(bookId);

        assertThat(result).isSameAs(bookDto);

        verify(service).findById(bookId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindBookById() {
        var bookId = randomUUID();

        when(service.findById(bookId))
                .thenThrow(EntityNotFoundException.ofBook(bookId));

        assertThatThrownBy(() -> controller.getBookById(bookId))
                .isNotNull()
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage(EntityNotFoundException.ERROR_MESSAGE, BookEntity.class.getSimpleName(), bookId);

        verify(service).findById(bookId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldCreateNewBook() {
        var bookDto1st = provideBookDto(randomUUID());
        var bookDto2nd = provideBookDto(randomUUID());

        when(service.create(bookDto1st))
                .thenReturn(bookDto2nd);

        var result = controller.createBook(bookDto1st);

        assertThat(result)
                .isNotNull()
                .isSameAs(bookDto2nd);

        verify(service).create(bookDto1st);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldUpdateExistingBook() {
        var bookId = randomUUID();
        var bookDto1st = provideBookDto(randomUUID());
        var bookDto2nd = provideBookDto(bookId);

        when(service.update(bookId, bookDto1st))
                .thenReturn(bookDto2nd);

        var result = controller.updateBook(bookId, bookDto1st);

        assertThat(result)
                .isNotNull()
                .isSameAs(bookDto2nd);

        verify(service).update(bookId, bookDto1st);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateBookById() {
        var bookId = randomUUID();
        var bookDto = provideBookDto(bookId);

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
