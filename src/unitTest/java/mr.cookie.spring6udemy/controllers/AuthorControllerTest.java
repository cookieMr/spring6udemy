package mr.cookie.spring6udemy.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.services.AuthorService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

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
        var pageAuthor = new PageImpl<>(List.of(AUTHOR_DTO));

        when(authorService.findAll(anyInt(), anyInt()))
                .thenReturn(pageAuthor);

        var result = authorController.getAllAuthors(1, 2);

        assertThat(result).isSameAs(pageAuthor);

        verify(authorService).findAll(1, 2);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldGetAuthorById() {
        when(authorService.findById(any(UUID.class))).thenReturn(Optional.of(AUTHOR_DTO));

        var result = authorController.getAuthorById(AUTHOR_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(AUTHOR_DTO);

        verify(authorService).findById(AUTHOR_ID);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        when(authorService.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorController.getAuthorById(AUTHOR_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(authorService).findById(AUTHOR_ID);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldCreateNewAuthor() {
        when(authorService.create(any(AuthorDto.class))).thenReturn(AUTHOR_DTO);

        var result = authorController.createAuthor(AUTHOR_DTO);

        assertThat(result)
                .isNotNull()
                .isSameAs(AUTHOR_DTO);

        verify(authorService).create(AUTHOR_DTO);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldUpdateExistingAuthor() {
        when(authorService.update(any(UUID.class), any(AuthorDto.class))).thenReturn(AUTHOR_DTO);

        var result = authorController.updateAuthor(AUTHOR_ID, AUTHOR_DTO);

        assertThat(result)
                .isNotNull()
                .isSameAs(AUTHOR_DTO);

        verify(authorService).update(AUTHOR_ID, AUTHOR_DTO);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        when(authorService.update(any(UUID.class), any(AuthorDto.class)))
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
        when(authorService.deleteById(any(UUID.class))).thenReturn(true);

        authorController.deleteAuthor(AUTHOR_ID);

        verify(authorService).deleteById(AUTHOR_ID);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteAuthorById() {
        when(authorService.deleteById(any(UUID.class))).thenReturn(false);

        assertThatThrownBy(() -> authorController.deleteAuthor(AUTHOR_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, AuthorDto.class.getSimpleName(), AUTHOR_ID);

        verify(authorService).deleteById(AUTHOR_ID);
        verifyNoMoreInteractions(authorService);
    }

}
