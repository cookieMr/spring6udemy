package mr.cookie.spring6udemy.units.controllers;

import mr.cookie.spring6udemy.controllers.BookController;
import mr.cookie.spring6udemy.model.model.Book;
import mr.cookie.spring6udemy.services.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @Test
    void shoutGetBooks() {
        var book = Book.builder().id(3L).build();

        when(this.bookService.findAll()).thenReturn(Collections.singletonList(book));

        var result = this.bookController.getBooks();

        assertThat(result).containsOnly(book);

        verify(this.bookService).findAll();
        verifyNoMoreInteractions(this.bookService);
    }

}
