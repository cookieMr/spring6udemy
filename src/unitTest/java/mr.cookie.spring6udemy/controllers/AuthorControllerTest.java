package mr.cookie.spring6udemy.controllers;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;
import mr.cookie.spring6udemy.exceptions.EntityNotFoundException;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.model.entities.AuthorEntity;
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
    private AuthorService service;

    @NotNull
    @InjectMocks
    private AuthorController controller;

    @Test
    void shouldGetAllAuthors() {
        var authorDto = AuthorDto.builder()
                .id(randomUUID())
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
        when(service.findAll())
                .thenReturn(Stream.of(authorDto));

        var result = controller.getAllAuthors();

        assertThat(result)
                .isEqualTo(ResponseEntity.ok(List.of(authorDto)));

        verify(service).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldGetAuthorById() {
        var authorId = randomUUID();
        var authorDto = AuthorDto.builder()
                .id(authorId)
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();

        when(service.findById(authorId)).thenReturn(authorDto);

        var result = controller.getAuthorById(authorId);

        assertThat(result).isSameAs(authorDto);

        verify(service).findById(authorId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        var authorId = randomUUID();
        when(service.findById(authorId)).thenThrow(EntityNotFoundException.ofAuthor(authorId));

        assertThatThrownBy(() -> controller.getAuthorById(authorId))
                .isNotNull()
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage(EntityNotFoundException.ERROR_MESSAGE, AuthorEntity.class.getSimpleName(), authorId);

        verify(service).findById(authorId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldCreateNewAuthor() {
        var authorDto1st = AuthorDto.builder()
                .id(randomUUID())
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
        var authorDto2nd = AuthorDto.builder()
                .id(randomUUID())
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
        when(service.create(authorDto1st)).thenReturn(authorDto2nd);

        var result = controller.createAuthor(authorDto1st);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.status(HttpStatus.CREATED).body(authorDto2nd));

        verify(service).create(authorDto1st);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldUpdateExistingAuthor() {
        var authorId = randomUUID();
        var authorDto1st = AuthorDto.builder()
                .id(randomUUID())
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
        var authorDto2nd = AuthorDto.builder()
                .id(randomUUID())
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
        when(service.update(authorId, authorDto1st)).thenReturn(authorDto2nd);

        var result = controller.updateAuthor(authorId, authorDto1st);

        assertThat(result)
                .isNotNull()
                .isEqualTo(ResponseEntity.ok(authorDto2nd));

        verify(service).update(authorId, authorDto1st);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        var authorId = randomUUID();
        var authorDto = AuthorDto.builder()
                .id(authorId)
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
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
