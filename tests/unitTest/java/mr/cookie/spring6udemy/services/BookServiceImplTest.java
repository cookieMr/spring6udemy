package mr.cookie.spring6udemy.services;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mr.cookie.spring6udemy.exceptions.EntityExistsException;
import mr.cookie.spring6udemy.exceptions.EntityNotFoundException;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.model.entities.BookEntity;
import mr.cookie.spring6udemy.model.mappers.BookMapper;
import mr.cookie.spring6udemy.model.mappers.BookMapperImpl;
import mr.cookie.spring6udemy.providers.entities.BookEntityProvider;
import mr.cookie.spring6udemy.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Spy
    private BookMapper mapper = new BookMapperImpl();

    @Mock
    private BookRepository repository;

    @InjectMocks
    private BookServiceImpl service;

    @Test
    void shouldReturnAllBooks() {
        var bookEntity = BookEntityProvider.provideBookEntity(randomUUID());

        when(repository.findAll())
                .thenReturn(List.of(bookEntity));

        var result = service.findAll();

        assertThat(result)
                .isNotNull()
                .hasSize(1);

        verify(repository).findAll();
        verify(mapper).map(bookEntity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldFindBookById() {
        var bookId = randomUUID();
        var bookEntity = BookEntityProvider.provideBookEntity(bookId);

        when(repository.findById(bookId))
                .thenReturn(Optional.of(bookEntity));

        var result = service.findById(bookId);

        assertThat(result)
                .isNotNull()
                .returns(bookId, BookDto::getId);

        verify(repository).findById(bookId);
        verify(mapper).map(bookEntity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindBookById() {
        var bookId = randomUUID();
        when(repository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(bookId))
                .isNotNull()
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage(EntityNotFoundException.ERROR_MESSAGE, BookEntity.class.getSimpleName(), bookId);

        verify(repository).findById(bookId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldCreateNewBook() {
        var bookId = randomUUID();
        var bookEntity = BookEntityProvider.provideBookEntity(bookId);
        var bookDto = BookDto.builder()
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .id(bookId)
                .build();

        when(repository.findByIsbn(bookDto.getIsbn()))
                .thenReturn(Optional.empty());
        when(repository.save(bookEntity)).thenReturn(bookEntity);

        var result = service.create(bookDto);

        assertThat(result)
                .isNotNull()
                .returns(bookId, BookDto::getId)
                .returns(bookEntity.getTitle(), BookDto::getTitle)
                .returns(bookEntity.getIsbn(), BookDto::getIsbn);

        verify(repository).findByIsbn(bookDto.getIsbn());
        verify(repository).save(bookEntity);
        verify(mapper).map(bookEntity);
        verify(mapper).map(bookDto);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldThrowExceptionWhenBookAlreadyExists() {
        var bookId = randomUUID();
        var bookEntity = BookEntityProvider.provideBookEntity(bookId);
        var bookDto = BookDto.builder()
                .title(randomAlphabetic(25))
                .isbn(bookEntity.getIsbn())
                .id(bookId)
                .build();

        when(repository.findByIsbn(bookDto.getIsbn()))
                .thenReturn(Optional.of(bookEntity));

        assertThatThrownBy(() -> service.create(bookDto))
                .isNotNull()
                .isExactlyInstanceOf(EntityExistsException.class)
                .hasMessage(EntityExistsException.ERROR_MESSAGE, BookEntity.class.getSimpleName());

        verify(repository).findByIsbn(bookDto.getIsbn());
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldUpdateExistingBook() {
        var bookId = randomUUID();
        var bookEntity = BookEntityProvider.provideBookEntity(bookId);
        var updatedBookDto = BookDto.builder()
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .id(bookId)
                .build();
        var updatedEntity = BookEntityProvider.provideBookEntity(bookId);

        when(repository.findByIsbn(updatedBookDto.getIsbn()))
                .thenReturn(Optional.empty());
        when(repository.findById(bookId)).thenReturn(Optional.of(bookEntity));
        when(repository.save(bookEntity)).thenReturn(bookEntity);

        var result = service.update(bookId, updatedBookDto);

        assertThat(result)
                .isNotNull()
                .isEqualTo(updatedBookDto);

        verify(repository).findByIsbn(updatedBookDto.getIsbn());
        verify(repository).findById(bookId);
        verify(repository).save(updatedEntity);
        verify(mapper).map(bookEntity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateBookById() {
        var bookId = randomUUID();
        when(repository.findById(bookId)).thenReturn(Optional.empty());

        var bookDto = BookDto.builder()
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .id(bookId)
                .build();
        assertThatThrownBy(() -> service.update(bookId, bookDto))
                .isNotNull()
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage(EntityNotFoundException.ERROR_MESSAGE, BookEntity.class.getSimpleName(), bookId);

        verify(repository).findById(bookId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldThrowExceptionWhenSameBookAlreadyExists() {
        var bookId = randomUUID();
        var bookDto = BookDto.builder()
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();
        var bookEntity = BookEntityProvider.provideBookEntity();

        when(repository.findById(bookId))
                .thenReturn(Optional.of(bookEntity));
        when(repository.findByIsbn(bookDto.getIsbn()))
                .thenReturn(Optional.of(bookEntity));

        assertThatThrownBy(() -> service.update(bookId, bookDto))
                .isNotNull()
                .isExactlyInstanceOf(EntityExistsException.class)
                .hasMessage(EntityExistsException.ERROR_MESSAGE, BookEntity.class.getSimpleName());

        verify(repository).findById(bookId);
        verify(repository).findByIsbn(bookDto.getIsbn());
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldDeleteExistingBook() {
        var bookId = randomUUID();

        service.deleteById(bookId);

        verify(repository).deleteById(bookId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

}
