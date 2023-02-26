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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    private static final long BOOK_ID = 1L;
    private static final BookDto BOOK_DTO = BookDto.builder().id(BOOK_ID).build();
    private static final Book BOOK = Book.builder().id(BOOK_ID).build();

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
        when(this.bookRepository.findAll())
                .thenReturn(Collections.singletonList(BOOK_DTO));

        var result = this.bookService.findAll();

        assertThat(result)
                .isNotNull()
                .hasSize(1)
                .containsOnly(Book.builder().id(BOOK_ID).build());

        verify(this.bookRepository).findAll();
        verify(this.bookMapper).mapToModel(anyIterable());
        verify(this.bookMapper).map(BOOK_DTO);
        verifyNoMoreInteractions(this.bookRepository, this.bookMapper);
    }

    @Test
    void shouldFindBookById() {
        when(this.bookRepository.findById(anyLong())).thenReturn(Optional.of(BOOK_DTO));

        var result = this.bookService.findById(BOOK_ID);

        assertThat(result)
                .isNotNull()
                .returns(BOOK_ID, Book::getId);

        verify(this.bookRepository).findById(BOOK_ID);
        verify(this.bookMapper).map(BOOK_DTO);
        verifyNoMoreInteractions(this.bookRepository, this.bookMapper);
    }

    @Test
    void shouldCreateNewBook() {
        when(this.bookRepository.save(any(BookDto.class))).thenReturn(BOOK_DTO);

        var result = this.bookService.create(BOOK);

        assertThat(result)
                .isNotNull()
                .returns(BOOK_ID, Book::getId);

        verify(this.bookRepository).save(BOOK_DTO);
        verify(this.bookMapper).map(BOOK_DTO);
        verify(this.bookMapper).map(BOOK);
        verifyNoMoreInteractions(this.bookRepository, this.bookMapper);
    }

}
