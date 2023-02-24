package mr.cookie.spring6udemy.units.services;

import mr.cookie.spring6udemy.domain.Book;
import mr.cookie.spring6udemy.repositories.BookRepository;
import mr.cookie.spring6udemy.services.BookServiceImpl;
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
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void shouldFindAllBooks() {
        var book = Book.builder().id(1L).build();

        when(this.bookRepository.findAll()).thenReturn(Collections.singletonList(book));

        var result = this.bookService.findAll();

        assertThat(result)
                .hasSize(1)
                .containsOnly(book);

        verify(this.bookRepository).findAll();
        verifyNoMoreInteractions(this.bookRepository);
    }

}
