package mr.cookie.spring6udemy.units.services;

import mr.cookie.spring6udemy.model.entities.BookDto;
import mr.cookie.spring6udemy.model.mappers.BookMapper;
import mr.cookie.spring6udemy.model.mappers.BookMapperImpl;
import mr.cookie.spring6udemy.model.model.Book;
import mr.cookie.spring6udemy.repositories.BookRepository;
import mr.cookie.spring6udemy.services.BookServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    private static final long ID = 1L;

    @Spy
    private BookMapper bookMapper = new BookMapperImpl();

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void shouldFindAllBooks() {
        var bookDto = BookDto.builder().id(ID).build();

        when(this.bookRepository.findAll()).thenReturn(Collections.singletonList(bookDto));

        var result = this.bookService.findAll();

        assertThat(result)
                .hasSize(1)
                .containsOnly(Book.builder().id(ID).build());

        verify(this.bookRepository).findAll();
        verify(this.bookMapper).mapToModel(anyIterable());
        verify(this.bookMapper).map(bookDto);
        verifyNoMoreInteractions(this.bookRepository, this.bookMapper);
    }

}
