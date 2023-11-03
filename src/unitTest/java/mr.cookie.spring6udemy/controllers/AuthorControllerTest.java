package mr.cookie.spring6udemy.controllers;

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

    private static final UUID AUTHOR_ID = UUID.randomUUID();
    private static final AuthorDto AUTHOR_DTO = AuthorDto.builder().id(AUTHOR_ID).build();

    @Mock
    @NotNull
    private AuthorService authorService;

    @NotNull
    @InjectMocks
    private AuthorController authorController;

    @Test
    void shouldGetAllAuthors() {
        when(authorService.findAll())
                .thenReturn(Stream.of(AUTHOR_DTO));

        var result = authorController.getAllAuthors();

        assertThat(result)
                .isEqualTo(ResponseEntity.ok(List.of(AUTHOR_DTO)));

        verify(authorService).findAll();
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldGetAuthorById() {
        when(authorService.findById(AUTHOR_ID)).thenReturn(Optional.of(AUTHOR_DTO));

        var result = authorController.getAuthorById(AUTHOR_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.ok(AUTHOR_DTO));

        verify(authorService).findById(AUTHOR_ID);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        when(authorService.findById(AUTHOR_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorController.getAuthorById(AUTHOR_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(authorService).findById(AUTHOR_ID);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldCreateNewAuthor() {
        when(authorService.create(AUTHOR_DTO)).thenReturn(AUTHOR_DTO);

        var result = authorController.createAuthor(AUTHOR_DTO);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.status(HttpStatus.CREATED).body(AUTHOR_DTO));

        verify(authorService).create(AUTHOR_DTO);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldUpdateExistingAuthor() {
        when(authorService.update(AUTHOR_ID, AUTHOR_DTO)).thenReturn(AUTHOR_DTO);

        var result = authorController.updateAuthor(AUTHOR_ID, AUTHOR_DTO);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.ok(AUTHOR_DTO));

        verify(authorService).update(AUTHOR_ID, AUTHOR_DTO);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        when(authorService.update(AUTHOR_ID, AUTHOR_DTO))
                .thenThrow(new NotFoundEntityException(AUTHOR_ID, AuthorDto.class));

        assertThatThrownBy(() -> authorController.updateAuthor(AUTHOR_ID, AUTHOR_DTO))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, AuthorDto.class.getSimpleName(), AUTHOR_ID);

        verify(authorService).update(AUTHOR_ID, AUTHOR_DTO);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldDeleteExistingAuthor() {
        when(authorService.deleteById(AUTHOR_ID)).thenReturn(true);

        authorController.deleteAuthor(AUTHOR_ID);

        verify(authorService).deleteById(AUTHOR_ID);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteAuthorById() {
        when(authorService.deleteById(AUTHOR_ID)).thenReturn(false);

        assertThatThrownBy(() -> authorController.deleteAuthor(AUTHOR_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, AuthorDto.class.getSimpleName(), AUTHOR_ID);

        verify(authorService).deleteById(AUTHOR_ID);
        verifyNoMoreInteractions(authorService);
    }

}
