package mr.cookie.spring6udemy.controllers;

import static java.util.UUID.randomUUID;
import static mr.cookie.spring6udemy.providers.dtos.AuthorDtoProvider.provideAuthorDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import mr.cookie.spring6udemy.exceptions.EntityNotFoundException;
import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import mr.cookie.spring6udemy.services.AuthorService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    @Mock
    @NotNull
    private AuthorService service;

    @NotNull
    @InjectMocks
    private AuthorController controller;

    @Test
    void shouldGetAllAuthors() {
        var authorDto = provideAuthorDto(randomUUID());

        when(service.findAll())
                .thenReturn(List.of(authorDto));

        var result = controller.getAllAuthors();

        assertThat(result)
                .isEqualTo(List.of(authorDto));

        verify(service).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldGetAuthorById() {
        var authorId = randomUUID();
        var authorDto = provideAuthorDto(authorId);

        when(service.findById(authorId))
                .thenReturn(authorDto);

        var result = controller.getAuthorById(authorId);

        assertThat(result).isSameAs(authorDto);

        verify(service).findById(authorId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        var authorId = randomUUID();

        when(service.findById(authorId))
                .thenThrow(EntityNotFoundException.ofAuthor(authorId));

        assertThatThrownBy(() -> controller.getAuthorById(authorId))
                .isNotNull()
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage(EntityNotFoundException.ERROR_MESSAGE, AuthorEntity.class.getSimpleName(), authorId);

        verify(service).findById(authorId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldCreateNewAuthor() {
        var authorDto1st = provideAuthorDto(randomUUID());
        var authorDto2nd = provideAuthorDto(randomUUID());

        when(service.create(authorDto1st))
                .thenReturn(authorDto2nd);

        var result = controller.createAuthor(authorDto1st);

        assertThat(result)
                .isNotNull()
                .isSameAs(authorDto2nd);

        verify(service).create(authorDto1st);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldUpdateExistingAuthor() {
        var authorId = randomUUID();
        var authorDto1st = provideAuthorDto(authorId);
        var authorDto2nd = provideAuthorDto(authorId);

        when(service.update(authorId, authorDto1st))
                .thenReturn(authorDto2nd);

        var result = controller.updateAuthor(authorId, authorDto1st);

        assertThat(result)
                .isNotNull()
                .isSameAs(authorDto2nd);

        verify(service).update(authorId, authorDto1st);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        var authorId = randomUUID();
        var authorDto = provideAuthorDto(authorId);

        when(service.update(authorId, authorDto))
                .thenThrow(EntityNotFoundException.ofAuthor(authorId));

        assertThatThrownBy(() -> controller.updateAuthor(authorId, authorDto))
                .isNotNull()
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage(EntityNotFoundException.ERROR_MESSAGE, AuthorEntity.class.getSimpleName(), authorId);

        verify(service).update(authorId, authorDto);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldDeleteExistingAuthor() {
        var authorId = randomUUID();

        controller.deleteAuthor(authorId);

        verify(service).deleteById(authorId);
        verifyNoMoreInteractions(service);
    }

}
