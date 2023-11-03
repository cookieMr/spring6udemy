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
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
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
    private ArgumentCaptor<BookEntity> bookDtoArgumentCaptor;

    @Spy
    private BookMapper bookMapper = new BookMapperImpl();

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void shouldReturnAllBooks() {
        var bookEntity = BookEntity.builder()
                .id(UUID.randomUUID())
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();

        when(bookRepository.findAll())
                .thenReturn(List.of(bookEntity));

        var result = bookService.findAll();

        assertThat(result)
                .isNotNull()
                .hasSize(1);

        verify(bookRepository).findAll();
        verify(bookMapper).map(bookEntity);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void shouldFindBookById() {
        var bookId = UUID.randomUUID();
        var bookEntity = BookEntity.builder()
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .id(bookId)
                .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));

        var result = bookService.findById(bookId);

        assertThat(result)
                .isNotNull()
                .isPresent()
                .get()
                .returns(bookId, BookDto::getId);

        verify(bookRepository).findById(bookId);
        verify(bookMapper).map(bookEntity);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindBookById() {
        var bookId = UUID.randomUUID();
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        var result = bookService.findById(bookId);

        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(bookRepository).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(bookMapper);
    }

    @Test
    void shouldCreateNewBook() {
        var bookId = UUID.randomUUID();
        var bookEntity = BookEntity.builder()
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .id(bookId)
                .build();

        when(bookRepository.save(bookEntity)).thenReturn(bookEntity);

        var bookDto = BookDto.builder()
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .id(bookId)
                .build();
        var result = bookService.create(bookDto);

        assertThat(result)
                .isNotNull()
                .returns(bookId, BookDto::getId);

        verify(bookRepository).save(bookEntity);
        verify(bookMapper).map(bookEntity);
        verify(bookMapper).map(bookDto);
        verifyNoMoreInteractions(bookRepository, bookMapper);
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

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
        when(bookRepository.save(bookEntity)).thenReturn(bookEntity);

        var result = bookService.update(bookId, updatedBookDto);

        assertThat(result)
                .isNotNull()
                .returns(bookId, BookDto::getId)
                .returns(updatedBookDto.getTitle(), BookDto::getTitle)
                .returns(updatedBookDto.getIsbn(), BookDto::getIsbn);

        verify(bookRepository).findById(bookId);
        verify(bookRepository).save(bookDtoArgumentCaptor.capture());
        verify(bookMapper).map(bookEntity);
        verifyNoMoreInteractions(bookRepository, bookMapper);

        assertThat(bookDtoArgumentCaptor.getValue())
                .isNotNull()
                .returns(bookId, BookEntity::getId)
                .returns(updatedBookDto.getTitle(), BookEntity::getTitle)
                .returns(updatedBookDto.getIsbn(), BookEntity::getIsbn);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateBookById() {
        var bookId = UUID.randomUUID();
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        var bookDto = BookDto.builder()
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .id(bookId)
                .build();
        assertThatThrownBy(() -> bookService.update(bookId, bookDto))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(bookRepository).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(bookMapper);
    }

    @Test
    void shouldDeleteExistingBook() {
        var bookId = UUID.randomUUID();
        when(bookRepository.existsById(bookId)).thenReturn(true);

        var result = bookService.deleteById(bookId);

        assertThat(result).isTrue();

        verify(bookRepository).deleteById(bookId);
        verify(bookRepository).existsById(bookId);
        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(bookMapper);
    }

    @Test
    void shouldNotDeleteNotExistingBook() {
        var bookId = UUID.randomUUID();
        when(bookRepository.existsById(bookId)).thenReturn(false);

        var result = bookService.deleteById(bookId);

        assertThat(result).isFalse();

        verify(bookRepository).existsById(bookId);
        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(bookMapper);
    }

}
