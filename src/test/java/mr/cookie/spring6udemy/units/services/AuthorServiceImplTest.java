package mr.cookie.spring6udemy.units.services;

import mr.cookie.spring6udemy.model.entities.AuthorDto;
import mr.cookie.spring6udemy.model.mappers.AuthorMapper;
import mr.cookie.spring6udemy.model.mappers.AuthorMapperImpl;
import mr.cookie.spring6udemy.model.model.Author;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import mr.cookie.spring6udemy.services.AuthorServiceImpl;
import mr.cookie.spring6udemy.services.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    private static final long AUTHOR_ID = 2L;
    private static final Author AUTHOR = Author.builder().id(AUTHOR_ID).build();
    private static final Supplier<AuthorDto> AUTHOR_DTO_SUPPLIER = () -> AuthorDto.builder().id(AUTHOR_ID).build();

    @Spy
    @NotNull
    private AuthorMapper authorMapper = new AuthorMapperImpl();

    @Mock
    @NotNull
    private AuthorRepository authorRepository;

    @NotNull
    @InjectMocks
    private AuthorServiceImpl authorService;

    @Captor
    private ArgumentCaptor<AuthorDto> authorDtoArgumentCaptor;

    @Test
    void shouldReturnAllAuthors() {
        var authorDto = AUTHOR_DTO_SUPPLIER.get();

        when(this.authorRepository.findAll())
                .thenReturn(Collections.singletonList(authorDto));

        var result = this.authorService.findAll();

        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .contains(Author.builder().id(AUTHOR_ID).build());

        verify(this.authorRepository).findAll();
        verify(this.authorMapper).mapToModel(anyIterable());
        verify(this.authorMapper).map(authorDto);
        verifyNoMoreInteractions(this.authorRepository, this.authorMapper);
    }

    @Test
    void shouldReturnAuthorById() {
        var authorDto = AUTHOR_DTO_SUPPLIER.get();

        when(this.authorRepository.findById(anyLong())).thenReturn(Optional.of(authorDto));

        var result = this.authorService.findById(AUTHOR_ID);

        assertThat(result)
                .isNotNull()
                .returns(AUTHOR_ID, Author::getId);

        verify(this.authorRepository).findById(AUTHOR_ID);
        verify(this.authorMapper).map(authorDto);
        verifyNoMoreInteractions(this.authorRepository, this.authorMapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        when(this.authorRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.authorService.findById(AUTHOR_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, Author.class.getSimpleName(), AUTHOR_ID);

        verify(this.authorRepository).findById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorRepository);
        verifyNoInteractions(this.authorMapper);
    }

    @Test
    void shouldCreateNewAuthor() {
        var authorDto = AUTHOR_DTO_SUPPLIER.get();

        when(this.authorRepository.save(any(AuthorDto.class))).thenReturn(authorDto);

        var result = this.authorService.create(AUTHOR);

        assertThat(result)
                .isNotNull()
                .returns(AUTHOR_ID, Author::getId);

        verify(this.authorRepository).save(authorDto);
        verify(this.authorMapper).map(authorDto);
        verify(this.authorMapper).map(AUTHOR);
        verifyNoMoreInteractions(this.authorRepository, this.authorMapper);
    }

    @Test
    void shouldUpdateExistingAuthor() {
        var authorDto = AUTHOR_DTO_SUPPLIER.get();
        var updatedAuthor = Author.builder()
                .firstName("Brandon")
                .lastName("Sanderson")
                .build();

        when(this.authorRepository.findById(anyLong())).thenReturn(Optional.of(authorDto));
        when(this.authorRepository.save(any(AuthorDto.class))).thenReturn(authorDto);

        var result = this.authorService.update(AUTHOR_ID, updatedAuthor);

        assertThat(result)
                .isNotNull()
                .returns(AUTHOR_ID, Author::getId)
                .returns(updatedAuthor.getFirstName(), Author::getFirstName)
                .returns(updatedAuthor.getLastName(), Author::getLastName);

        verify(this.authorRepository).findById(AUTHOR_ID);
        verify(this.authorRepository).save(this.authorDtoArgumentCaptor.capture());
        verify(this.authorMapper).map(authorDto);
        verifyNoMoreInteractions(this.authorRepository, this.authorMapper);

        assertThat(this.authorDtoArgumentCaptor.getValue())
                .isNotNull()
                .returns(AUTHOR_ID, AuthorDto::getId)
                .returns(updatedAuthor.getFirstName(), AuthorDto::getFirstName)
                .returns(updatedAuthor.getLastName(), AuthorDto::getLastName);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        when(this.authorRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.authorService.update(AUTHOR_ID, AUTHOR))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, Author.class.getSimpleName(), AUTHOR_ID);

        verify(this.authorRepository).findById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorRepository);
        verifyNoInteractions(this.authorMapper);
    }

    @Test
    void shouldDeleteExistingAuthor() {
        this.authorService.deleteById(AUTHOR_ID);

        verify(this.authorRepository).deleteById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorRepository);
        verifyNoInteractions(this.authorMapper);
    }

}
