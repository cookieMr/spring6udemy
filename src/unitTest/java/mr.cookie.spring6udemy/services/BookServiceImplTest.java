package mr.cookie.spring6udemy.services;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mr.cookie.spring6udemy.exceptions.EntityNotFoundException;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.model.entities.BookEntity;
import mr.cookie.spring6udemy.model.mappers.BookMapper;
import mr.cookie.spring6udemy.model.mappers.BookMapperImpl;
import mr.cookie.spring6udemy.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Captor
    private ArgumentCaptor<BookEntity> captor;

    @Spy
    private BookMapper mapper = new BookMapperImpl();

    @Mock
    private BookRepository repository;

    @InjectMocks
    private BookServiceImpl service;

    @Test
    void shouldReturnAllBooks() {
        var bookEntity = BookEntity.builder()
                .id(UUID.randomUUID())
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();

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
        var bookId = UUID.randomUUID();
        var bookEntity = BookEntity.builder()
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .id(bookId)
                .build();

        when(repository.findById(bookId)).thenReturn(Optional.of(bookEntity));

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
        var bookId = UUID.randomUUID();
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
        var bookId = UUID.randomUUID();
        var bookEntity = BookEntity.builder()
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .id(bookId)
                .build();

        when(repository.save(bookEntity)).thenReturn(bookEntity);

        var bookDto = BookDto.builder()
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .id(bookId)
                .build();
        var result = service.create(bookDto);

        assertThat(result)
                .isNotNull()
                .returns(bookId, BookDto::getId);

        verify(repository).save(bookEntity);
        verify(mapper).map(bookEntity);
        verify(mapper).map(bookDto);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldUpdateExistingBook() {
        var bookId = UUID.randomUUID();
        var bookEntity = BookEntity.builder()
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .id(bookId)
                .build();

        var updatedBookDto = BookDto.builder()
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .id(bookId)
                .build();

        when(repository.findById(bookId)).thenReturn(Optional.of(bookEntity));
        when(repository.save(bookEntity)).thenReturn(bookEntity);

        var result = service.update(bookId, updatedBookDto);

        assertThat(result)
                .isNotNull()
                .returns(bookId, BookDto::getId)
                .returns(updatedBookDto.getTitle(), BookDto::getTitle)
                .returns(updatedBookDto.getIsbn(), BookDto::getIsbn);

        verify(repository).findById(bookId);
        verify(repository).save(captor.capture());
        verify(mapper).map(bookEntity);
        verifyNoMoreInteractions(repository, mapper);

        assertThat(captor.getValue())
                .isNotNull()
                .returns(bookId, BookEntity::getId)
                .returns(updatedBookDto.getTitle(), BookEntity::getTitle)
                .returns(updatedBookDto.getIsbn(), BookEntity::getIsbn);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateBookById() {
        var bookId = UUID.randomUUID();
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
    void shouldDeleteExistingBook() {
        var bookId = UUID.randomUUID();
        when(repository.existsById(bookId)).thenReturn(true);

        var result = service.deleteById(bookId);

        assertThat(result).isTrue();

        verify(repository).deleteById(bookId);
        verify(repository).existsById(bookId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldNotDeleteNotExistingBook() {
        var bookId = UUID.randomUUID();
        when(repository.existsById(bookId)).thenReturn(false);

        var result = service.deleteById(bookId);

        assertThat(result).isFalse();

        verify(repository).existsById(bookId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

}
