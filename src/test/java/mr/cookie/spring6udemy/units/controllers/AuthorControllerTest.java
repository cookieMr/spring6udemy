package mr.cookie.spring6udemy.units.controllers;

import mr.cookie.spring6udemy.controllers.AuthorController;
import mr.cookie.spring6udemy.model.model.Author;
import mr.cookie.spring6udemy.services.AuthorService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @NotNull
    private static Stream<Author> authorStream() {
        return Stream.of(AUTHOR);
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("authorStream")
    void shouldGetAuthorById(@Nullable Author author) {
        when(this.authorService.findById(anyLong())).thenReturn(author);

        var result = this.authorController.getAuthorById(AUTHOR_ID);

        assertThat(result)
                .isEqualTo(author);

        verify(this.authorService).findById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldCreateNewAuthor() {
        when(this.authorService.create(any(Author.class))).thenReturn(AUTHOR);

        var result = this.authorController.createNewAuthor(AUTHOR);

        assertThat(result)
                .isNotNull()
                .isSameAs(AUTHOR);

        verify(this.authorService).create(AUTHOR);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldUpdateExistingAuthor() {
        when(this.authorService.update(anyLong(), any(Author.class))).thenReturn(AUTHOR);

        var result = this.authorController.updateExistingAuthor(AUTHOR_ID, AUTHOR);

        assertThat(result)
                .isNotNull()
                .isSameAs(AUTHOR);

        verify(this.authorService).update(AUTHOR_ID, AUTHOR);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldDeleteExistingAuthor() {
        this.authorController.deleteAuthor(AUTHOR_ID);

        verify(this.authorService).deleteById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

}
