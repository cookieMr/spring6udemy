package mr.cookie.spring6udemy.units.bootstrap;

import mr.cookie.spring6udemy.bootstrap.BootstrapData;
import mr.cookie.spring6udemy.domain.Author;
import mr.cookie.spring6udemy.domain.Book;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import mr.cookie.spring6udemy.repositories.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootstrapDataTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BootstrapData bootstrapData;

    @Test
    void run() {
        var savedAuthor = Author.builder().id(42L).build();
        var savedBook1 = Book.builder().id(1L).build();
        var savedBook2 = Book.builder().id(2L).build();
        var savedBook3 = Book.builder().id(3L).build();
        var savedBook4 = Book.builder().id(4L).build();

        when(this.authorRepository.save(any(Author.class)))
                .thenReturn(savedAuthor);
        when(this.bookRepository.save(any(Book.class)))
                .thenReturn(savedBook1, savedBook2, savedBook3, savedBook4);

        this.bootstrapData.run();

        Assertions.assertThat(savedAuthor.getBooks())
                .containsExactlyInAnyOrder(savedBook1, savedBook2, savedBook3, savedBook4);

        verify(this.authorRepository, times(2)).save(any(Author.class));
        verify(this.authorRepository).count();
        verify(this.bookRepository, times(4)).save(any(Book.class));
        verify(this.bookRepository).count();
        verifyNoMoreInteractions(this.authorRepository, bookRepository);
    }

}
