package mr.cookie.spring6udemy.units.controllers;

import mr.cookie.spring6udemy.controllers.AuthorController;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.services.AuthorService;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
        when(this.authorService.findAll()).thenReturn(Collections.singletonList(AUTHOR_DTO));

        var result = this.authorController.getAllAuthors();

        assertThat(result)
                .isNotNull()
                .containsOnly(AUTHOR_DTO);

        verify(this.authorService).findAll();
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldGetAuthorById() {
        when(this.authorService.findById(any(UUID.class))).thenReturn(Optional.of(AUTHOR_DTO));

        var result = this.authorController.getAuthorById(AUTHOR_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(AUTHOR_DTO);

        verify(this.authorService).findById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        when(this.authorService.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.authorController.getAuthorById(AUTHOR_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(this.authorService).findById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldCreateNewAuthor() {
        when(this.authorService.create(any(AuthorDto.class))).thenReturn(AUTHOR_DTO);

        var result = this.authorController.createAuthor(AUTHOR_DTO);

        assertThat(result)
                .isNotNull()
                .isSameAs(AUTHOR_DTO);

        verify(this.authorService).create(AUTHOR_DTO);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldUpdateExistingAuthor() {
        when(this.authorService.update(any(UUID.class), any(AuthorDto.class))).thenReturn(AUTHOR_DTO);

        var result = this.authorController.updateAuthor(AUTHOR_ID, AUTHOR_DTO);

        assertThat(result)
                .isNotNull()
                .isSameAs(AUTHOR_DTO);

        verify(this.authorService).update(AUTHOR_ID, AUTHOR_DTO);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        when(this.authorService.update(any(UUID.class), any(AuthorDto.class)))
                .thenThrow(new NotFoundEntityException(AUTHOR_ID, AuthorDto.class));

        assertThatThrownBy(() -> this.authorController.updateAuthor(AUTHOR_ID, AUTHOR_DTO))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, AuthorDto.class.getSimpleName(), AUTHOR_ID);

        verify(this.authorService).update(AUTHOR_ID, AUTHOR_DTO);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldDeleteExistingAuthor() {
        when(this.authorService.deleteById(any(UUID.class))).thenReturn(true);

        this.authorController.deleteAuthor(AUTHOR_ID);

        verify(this.authorService).deleteById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteAuthorById() {
        doThrow(new NotFoundEntityException(AUTHOR_ID, AuthorDto.class))
                .when(this.authorService).deleteById(any(UUID.class));

        assertThatThrownBy(() -> this.authorController.deleteAuthor(AUTHOR_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, AuthorDto.class.getSimpleName(), AUTHOR_ID);

        verify(this.authorService).deleteById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

}
