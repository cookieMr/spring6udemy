package mr.cookie.spring6udemy.units.services;

import mr.cookie.spring6udemy.model.entities.BookDto;
import mr.cookie.spring6udemy.model.mappers.BookMapper;
import mr.cookie.spring6udemy.model.mappers.BookMapperImpl;
import mr.cookie.spring6udemy.model.model.Book;
import mr.cookie.spring6udemy.repositories.BookRepository;
import mr.cookie.spring6udemy.services.BookServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    private static final long BOOK_ID = 1L;

    @Spy
    @NotNull
    private BookMapper bookMapper = new BookMapperImpl();

    @Mock
    @NotNull
    private BookRepository bookRepository;

    @NotNull
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void shouldFindAllBooks() {
        var bookDto = BookDto.builder().id(BOOK_ID).build();

        when(this.bookRepository.findAll())
                .thenReturn(Collections.singletonList(bookDto));

        var result = this.bookService.findAll();

        assertThat(result)
                .isNotNull()
                .hasSize(1)
                .containsOnly(Book.builder().id(BOOK_ID).build());

        verify(this.bookRepository).findAll();
        verify(this.bookMapper).mapToModel(anyIterable());
        verify(this.bookMapper).map(bookDto);
        verifyNoMoreInteractions(this.bookRepository, this.bookMapper);
    }

    @Test
    void shouldFindBookById() {
        var bookDto = BookDto.builder().id(BOOK_ID).build();

        when(this.bookRepository.findById(anyLong())).thenReturn(Optional.of(bookDto));

        var result = this.bookService.findById(BOOK_ID);

        assertThat(result)
                .isNotNull()
                .returns(BOOK_ID, Book::getId);

        verify(this.bookRepository).findById(BOOK_ID);
        verify(this.bookMapper).map(bookDto);
        verifyNoMoreInteractions(this.bookRepository, this.bookMapper);
    }

}
