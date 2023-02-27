package mr.cookie.spring6udemy.units.controllers;

import mr.cookie.spring6udemy.controllers.AuthorController;
import mr.cookie.spring6udemy.model.model.Author;
import mr.cookie.spring6udemy.services.AuthorService;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    private static final long AUTHOR_ID = 7L;
    private static final Author AUTHOR = Author.builder().id(AUTHOR_ID).build();

    @Mock
    @NotNull
    private AuthorService authorService;

    @NotNull
    @InjectMocks
    private AuthorController authorController;

    @Test
    void shouldGetAllAuthors() {
        when(this.authorService.findAll()).thenReturn(Collections.singletonList(AUTHOR));

        var result = this.authorController.getAllAuthors();

        assertThat(result)
                .isNotNull()
                .containsOnly(AUTHOR);

        verify(this.authorService).findAll();
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldGetAuthorById() {
        when(this.authorService.findById(anyLong())).thenReturn(AUTHOR);

        var result = this.authorController.getAuthorById(AUTHOR_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(AUTHOR);

        verify(this.authorService).findById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        when(this.authorService.findById(anyLong()))
                .thenThrow(new NotFoundEntityException(AUTHOR_ID, Author.class));

        assertThatThrownBy(() -> this.authorController.getAuthorById(AUTHOR_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, Author.class.getSimpleName(), AUTHOR_ID);

        verify(this.authorService).findById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldCreateNewAuthor() {
        when(this.authorService.create(any(Author.class))).thenReturn(AUTHOR);

        var result = this.authorController.createAuthor(AUTHOR);

        assertThat(result)
                .isNotNull()
                .isSameAs(AUTHOR);

        verify(this.authorService).create(AUTHOR);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldUpdateExistingAuthor() {
        when(this.authorService.update(anyLong(), any(Author.class))).thenReturn(AUTHOR);

        var result = this.authorController.updateAuthor(AUTHOR_ID, AUTHOR);

        assertThat(result)
                .isNotNull()
                .isSameAs(AUTHOR);

        verify(this.authorService).update(AUTHOR_ID, AUTHOR);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        when(this.authorService.update(anyLong(), any(Author.class)))
                .thenThrow(new NotFoundEntityException(AUTHOR_ID, Author.class));

        assertThatThrownBy(() -> this.authorController.updateAuthor(AUTHOR_ID, AUTHOR))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, Author.class.getSimpleName(), AUTHOR_ID);

        verify(this.authorService).update(AUTHOR_ID, AUTHOR);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldDeleteExistingAuthor() {
        this.authorController.deleteAuthor(AUTHOR_ID);

        verify(this.authorService).deleteById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteAuthorById() {
        doThrow(new NotFoundEntityException(AUTHOR_ID, Author.class))
                .when(this.authorService).deleteById(anyLong());

        assertThatThrownBy(() -> this.authorController.deleteAuthor(AUTHOR_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, Author.class.getSimpleName(), AUTHOR_ID);

        verify(this.authorService).deleteById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

}
