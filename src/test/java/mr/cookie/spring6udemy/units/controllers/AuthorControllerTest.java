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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    private static final long ID = 7L;
    private static final Author AUTHOR = Author.builder().id(ID).build();

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

        assertThat(result).containsOnly(AUTHOR);

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

        var result = this.authorController.getAuthorById(ID);

        assertThat(result).isEqualTo(author);

        verify(this.authorService).findById(ID);
        verifyNoMoreInteractions(this.authorService);
    }

}
