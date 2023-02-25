package mr.cookie.spring6udemy.units.controllers;

import mr.cookie.spring6udemy.controllers.AuthorController;
import mr.cookie.spring6udemy.model.model.Author;
import mr.cookie.spring6udemy.services.AuthorService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorControllerTest {

    @Mock
    @NotNull
    private AuthorService authorService;

    @NotNull
    @InjectMocks
    private AuthorController authorController;

    @Test
    void shouldGetAllAuthors() {
        var author = Author.builder().id(3L).build();

        when(this.authorService.findAll()).thenReturn(Collections.singletonList(author));

        var result = this.authorController.getAllAuthors();

        assertThat(result).containsOnly(author);

        verify(this.authorService).findAll();
        verifyNoMoreInteractions(this.authorService);
    }

}
