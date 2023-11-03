package mr.cookie.spring6udemy.controllers;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.services.AuthorService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    @Mock
    @NotNull
    private AuthorService authorService;

    @NotNull
    @InjectMocks
    private AuthorController authorController;

    @Test
    void shouldGetAllAuthors() {
        var authorDto = AuthorDto.builder()
                .id(UUID.randomUUID())
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
        when(authorService.findAll())
                .thenReturn(Stream.of(authorDto));

        var result = authorController.getAllAuthors();

        assertThat(result)
                .isEqualTo(ResponseEntity.ok(List.of(authorDto)));

        verify(authorService).findAll();
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldGetAuthorById() {
        var authorId = UUID.randomUUID();
        var authorDto = AuthorDto.builder()
                .id(authorId)
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();

        when(authorService.findById(authorId)).thenReturn(Optional.of(authorDto));

        var result = authorController.getAuthorById(authorId);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.ok(authorDto));

        verify(authorService).findById(authorId);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        var authorId = UUID.randomUUID();
        when(authorService.findById(authorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorController.getAuthorById(authorId))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(authorService).findById(authorId);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldCreateNewAuthor() {
        var authorDto1st = AuthorDto.builder()
                .id(UUID.randomUUID())
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
        var authorDto2nd = AuthorDto.builder()
                .id(UUID.randomUUID())
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
        when(authorService.create(authorDto1st)).thenReturn(authorDto2nd);

        var result = authorController.createAuthor(authorDto1st);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.status(HttpStatus.CREATED).body(authorDto2nd));

        verify(authorService).create(authorDto1st);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldUpdateExistingAuthor() {
        var authorId = UUID.randomUUID();
        var authorDto1st = AuthorDto.builder()
                .id(UUID.randomUUID())
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
        var authorDto2nd = AuthorDto.builder()
                .id(UUID.randomUUID())
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
        when(authorService.update(authorId, authorDto1st)).thenReturn(authorDto2nd);

        var result = authorController.updateAuthor(authorId, authorDto1st);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.ok(authorDto2nd));

        verify(authorService).update(authorId, authorDto1st);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        var authorId = UUID.randomUUID();
        var authorDto = AuthorDto.builder()
                .id(authorId)
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
        when(authorService.update(authorId, authorDto))
                .thenThrow(new NotFoundEntityException(authorId, AuthorDto.class));

        assertThatThrownBy(() -> authorController.updateAuthor(authorId, authorDto))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, AuthorDto.class.getSimpleName(), authorId);

        verify(authorService).update(authorId, authorDto);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldDeleteExistingAuthor() {
        var authorId = UUID.randomUUID();
        when(authorService.deleteById(authorId)).thenReturn(true);

        var result = authorController.deleteAuthor(authorId);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.noContent().build());

        verify(authorService).deleteById(authorId);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteAuthorById() {
        var authorId = UUID.randomUUID();
        when(authorService.deleteById(authorId)).thenReturn(false);

        assertThatThrownBy(() -> authorController.deleteAuthor(authorId))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, AuthorDto.class.getSimpleName(), authorId);

        verify(authorService).deleteById(authorId);
        verifyNoMoreInteractions(authorService);
    }

}
