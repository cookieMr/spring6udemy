package mr.cookie.spring6udemy.services;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mr.cookie.spring6udemy.exceptions.EntityExistsException;
import mr.cookie.spring6udemy.exceptions.EntityNotFoundException;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import mr.cookie.spring6udemy.model.mappers.AuthorMapper;
import mr.cookie.spring6udemy.model.mappers.AuthorMapperImpl;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Spy
    private AuthorMapper mapper = new AuthorMapperImpl();

    @Mock
    private AuthorRepository repository;

    @InjectMocks
    private AuthorServiceImpl service;

    @Test
    void shouldReturnAllAuthors() {
        var authorEntity = AuthorEntity.builder()
                .id(randomUUID())
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();

        when(repository.findAll())
                .thenReturn(List.of(authorEntity));

        var result = service.findAll();

        assertThat(result)
                .isNotNull()
                .hasSize(1);

        verify(repository).findAll();
        verify(mapper).map(authorEntity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldReturnAuthorById() {
        var authorId = randomUUID();
        var authorEntity = AuthorEntity.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .id(authorId)
                .build();

        when(repository.findById(authorId))
                .thenReturn(Optional.of(authorEntity));

        var result = service.findById(authorId);

        assertThat(result)
                .isNotNull()
                .returns(authorId, AuthorDto::getId);

        verify(repository).findById(authorId);
        verify(mapper).map(authorEntity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        var authorId = randomUUID();
        when(repository.findById(authorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(authorId))
                .isNotNull()
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage(EntityNotFoundException.ERROR_MESSAGE, AuthorEntity.class.getSimpleName(), authorId);

        verify(repository).findById(authorId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldCreateNewAuthor() {
        var authorId = randomUUID();
        var authorEntity = AuthorEntity.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .id(authorId)
                .build();
        var authorDto = AuthorDto.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .id(authorId)
                .build();

        when(repository.findByFirstNameAndLastName(
                authorDto.getFirstName(),
                authorDto.getLastName()))
                .thenReturn(Optional.empty());
        when(repository.save(authorEntity)).thenReturn(authorEntity);

        var result = service.create(authorDto);

        assertThat(result)
                .isNotNull()
                .returns(authorId, AuthorDto::getId)
                .returns(authorEntity.getFirstName(), AuthorDto::getFirstName)
                .returns(authorEntity.getLastName(), AuthorDto::getLastName);

        verify(repository).findByFirstNameAndLastName(
                authorDto.getFirstName(),
                authorDto.getLastName());
        verify(repository).save(authorEntity);
        verify(mapper).map(authorEntity);
        verify(mapper).map(authorDto);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldReturnExistingEntityWhenCreatingSameAuthor() {
        var authorId = randomUUID();
        var authorEntity = AuthorEntity.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .id(authorId)
                .build();
        var authorDto = AuthorDto.builder()
                .firstName(authorEntity.getFirstName())
                .lastName(authorEntity.getLastName())
                .id(authorId)
                .build();

        when(repository.findByFirstNameAndLastName(
                authorDto.getFirstName(),
                authorDto.getLastName()))
                .thenReturn(Optional.of(authorEntity));

        var result = service.create(authorDto);

        assertThat(result)
                .isNotNull()
                .isEqualTo(authorDto);

        verify(repository).findByFirstNameAndLastName(
                authorDto.getFirstName(),
                authorDto.getLastName());
        verify(mapper).map(authorEntity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldUpdateExistingAuthor() {
        var authorId = randomUUID();
        var authorEntity = AuthorEntity.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .id(authorId)
                .build();
        var updatedAuthorDto = AuthorDto.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .id(authorId)
                .build();
        var updatedEntity = AuthorEntity.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .id(authorId)
                .build();

        when(repository.findByFirstNameAndLastName(
                updatedAuthorDto.getFirstName(),
                updatedAuthorDto.getLastName()))
                .thenReturn(Optional.empty());
        when(repository.findById(authorId)).thenReturn(Optional.of(authorEntity));
        when(repository.save(authorEntity)).thenReturn(authorEntity);

        var result = service.update(authorId, updatedAuthorDto);

        assertThat(result)
                .isNotNull()
                .isEqualTo(updatedAuthorDto);

        verify(repository).findByFirstNameAndLastName(
                updatedAuthorDto.getFirstName(),
                updatedAuthorDto.getLastName());
        verify(repository).findById(authorId);
        verify(repository).save(updatedEntity);
        verify(mapper).map(authorEntity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        var authorId = randomUUID();
        when(repository.findById(authorId)).thenReturn(Optional.empty());

        var authorDto = AuthorDto.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();

        assertThatThrownBy(() -> service.update(authorId, authorDto))
                .isNotNull()
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage(EntityNotFoundException.ERROR_MESSAGE, AuthorEntity.class.getSimpleName(), authorId);

        verify(repository).findById(authorId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldThrowExceptionWhenSameAuthorAlreadyExists() {
        var authorId = randomUUID();
        var authorDto = AuthorDto.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
        var authorEntity = AuthorEntity.builder()
                .firstName(authorDto.getFirstName())
                .lastName(authorDto.getLastName())
                .build();

        when(repository.findById(authorId))
                .thenReturn(Optional.of(authorEntity));
        when(repository.findByFirstNameAndLastName(
                authorDto.getFirstName(),
                authorDto.getLastName()))
                .thenReturn(Optional.of(authorEntity));

        assertThatThrownBy(() -> service.update(authorId, authorDto))
                .isNotNull()
                .isExactlyInstanceOf(EntityExistsException.class)
                .hasMessage(EntityExistsException.ERROR_MESSAGE, AuthorEntity.class.getSimpleName());

        verify(repository).findById(authorId);
        verify(repository).findByFirstNameAndLastName(
                authorDto.getFirstName(),
                authorDto.getLastName());
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldDeleteExistingAuthor() {
        var authorId = randomUUID();

        service.deleteById(authorId);

        verify(repository).deleteById(authorId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

}
