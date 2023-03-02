package mr.cookie.spring6udemy.units.bootstrap;

import mr.cookie.spring6udemy.bootstrap.BootstrapData;
import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import mr.cookie.spring6udemy.model.entities.BookEntity;
import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import mr.cookie.spring6udemy.repositories.BookRepository;
import mr.cookie.spring6udemy.repositories.PublisherRepository;
import org.jetbrains.annotations.NotNull;
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
    @NotNull
    private AuthorRepository authorRepository;

    @Mock
    @NotNull
    private BookRepository bookRepository;

    @Mock
    @NotNull
    private PublisherRepository publisherRepository;

    @NotNull
    @InjectMocks
    private BootstrapData bootstrapData;

    @Captor
    @NotNull
    private ArgumentCaptor<List<BookEntity>> booksCaptor;

    @Test
    void shouldInitEntities() {
        var savedPublisher = PublisherEntity.builder().id(111L).build();
        var savedAuthor = AuthorEntity.builder().id(42L).build();
        var savedBook1 = BookEntity.builder().id(1L).build();
        var savedBook2 = BookEntity.builder().id(2L).build();
        var savedBook3 = BookEntity.builder().id(3L).build();
        var savedBook4 = BookEntity.builder().id(4L).build();

        when(this.publisherRepository.save(any(PublisherEntity.class)))
                .thenReturn(savedPublisher);
        when(this.bookRepository.saveAll(this.booksCaptor.capture()))
                .thenReturn(Arrays.asList(savedBook1, savedBook2, savedBook3, savedBook4));
        when(this.authorRepository.save(any(AuthorEntity.class)))
                .thenReturn(savedAuthor);

        this.bootstrapData.run();

        verify(this.authorRepository).save(any(AuthorEntity.class));
        verify(this.authorRepository).count();
        verify(this.bookRepository, times(2)).saveAll(anyList());
        verify(this.bookRepository).count();
        verify(this.publisherRepository).save(any(PublisherEntity.class));
        verify(this.publisherRepository).count();
        verifyNoMoreInteractions(this.authorRepository, this.bookRepository, this.publisherRepository);

        assertThat(this.booksCaptor.getAllValues())
                .isNotNull()
                .hasSize(2)
                .allMatch(books -> books.size() == 4);
        assertThat(this.booksCaptor.getAllValues().get(0))
                .isNotNull()
                .allMatch(book -> book.getPublisher().equals(savedPublisher));
        assertThat(this.booksCaptor.getAllValues().get(1))
                .isNotNull()
                .allMatch(book -> book.getAuthors().size() == 1)
                .allMatch(book -> book.getAuthors().contains(savedAuthor));
    }

}
