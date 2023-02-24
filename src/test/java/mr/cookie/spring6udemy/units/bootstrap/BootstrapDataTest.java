package mr.cookie.spring6udemy.units.bootstrap;

import mr.cookie.spring6udemy.bootstrap.BootstrapData;
import mr.cookie.spring6udemy.domain.Author;
import mr.cookie.spring6udemy.domain.Book;
import mr.cookie.spring6udemy.domain.Publisher;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import mr.cookie.spring6udemy.repositories.BookRepository;
import mr.cookie.spring6udemy.repositories.PublisherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private BootstrapData bootstrapData;

    @Captor
    private ArgumentCaptor<List<Book>> booksCaptor;

    @Test
    void run() {
        var savedPublisher = Publisher.builder().id(111L).build();
        var savedAuthor = Author.builder().id(42L).build();
        var savedBook1 = Book.builder().id(1L).build();
        var savedBook2 = Book.builder().id(2L).build();
        var savedBook3 = Book.builder().id(3L).build();
        var savedBook4 = Book.builder().id(4L).build();

        when(this.publisherRepository.save(any(Publisher.class)))
                .thenReturn(savedPublisher);
        when(this.bookRepository.saveAll(this.booksCaptor.capture()))
                .thenReturn(Arrays.asList(savedBook1, savedBook2, savedBook3, savedBook4));
        when(this.authorRepository.save(any(Author.class)))
                .thenReturn(savedAuthor);

        this.bootstrapData.run();

        verify(this.authorRepository).save(any(Author.class));
        verify(this.authorRepository).count();
        verify(this.bookRepository, times(2)).saveAll(anyList());
        verify(this.bookRepository).count();
        verify(this.publisherRepository).save(any(Publisher.class));
        verify(this.publisherRepository).count();
        verifyNoMoreInteractions(this.authorRepository, this.bookRepository, this.publisherRepository);

        assertThat(this.booksCaptor.getAllValues())
                .hasSize(2)
                .allMatch(books -> books.size() == 4);
        assertThat(this.booksCaptor.getAllValues().get(0))
                .allMatch(book -> book.getPublisher().equals(savedPublisher));
        assertThat(this.booksCaptor.getAllValues().get(1))
                .allMatch(book -> book.getAuthors().size() == 1)
                .allMatch(book -> book.getAuthors().contains(savedAuthor));
    }

}
