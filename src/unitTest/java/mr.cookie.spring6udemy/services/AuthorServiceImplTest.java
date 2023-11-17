package mr.cookie.spring6udemy.services;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mr.cookie.spring6udemy.exceptions.EntityNotFoundException;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import mr.cookie.spring6udemy.model.mappers.AuthorMapper;
import mr.cookie.spring6udemy.model.mappers.AuthorMapperImpl;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Captor
    private ArgumentCaptor<AuthorEntity> captor;

    @Spy
    private AuthorMapper mapper = new AuthorMapperImpl();

    @Mock
    private AuthorRepository repository;

    @InjectMocks
    private AuthorServiceImpl service;

    @Test
    void shouldReturnAllAuthors() {
        var authorEntity = AuthorEntity.builder()
                .id(UUID.randomUUID())
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
        var authorId = UUID.randomUUID();
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
        var authorId = UUID.randomUUID();
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
        var authorId = UUID.randomUUID();
        var authorEntity = AuthorEntity.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .id(authorId)
                .build();

        when(repository.save(authorEntity)).thenReturn(authorEntity);

        var authorDto = AuthorDto.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .id(authorId)
                .build();
        var result = service.create(authorDto);

        assertThat(result)
                .isNotNull()
                .returns(authorId, AuthorDto::getId);

        verify(repository).save(authorEntity);
        verify(mapper).map(authorEntity);
        verify(mapper).map(authorDto);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldUpdateExistingAuthor() {
        var authorId = UUID.randomUUID();
        var authorEntity = AuthorEntity.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .id(authorId)
                .build();
        var updatedAuthorDto = AuthorDto.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();

        when(repository.findById(authorId)).thenReturn(Optional.of(authorEntity));
        when(repository.save(authorEntity)).thenReturn(authorEntity);

        var result = service.update(authorId, updatedAuthorDto);

        assertThat(result)
                .isNotNull()
                .returns(authorId, AuthorDto::getId)
                .returns(updatedAuthorDto.getFirstName(), AuthorDto::getFirstName)
                .returns(updatedAuthorDto.getLastName(), AuthorDto::getLastName);

        verify(repository).findById(authorId);
        verify(repository).save(captor.capture());
        verify(mapper).map(authorEntity);
        verifyNoMoreInteractions(repository, mapper);

        assertThat(captor.getValue())
                .isNotNull()
                .returns(authorId, AuthorEntity::getId)
                .returns(updatedAuthorDto.getFirstName(), AuthorEntity::getFirstName)
                .returns(updatedAuthorDto.getLastName(), AuthorEntity::getLastName);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        var authorId = UUID.randomUUID();
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
    void shouldDeleteExistingAuthor() {
        var authorId = UUID.randomUUID();
        when(repository.existsById(authorId)).thenReturn(true);

        var result = service.deleteById(authorId);

        assertThat(result).isTrue();

        verify(repository).deleteById(authorId);
        verify(repository).existsById(authorId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldNotDeleteNotExistingAuthor() {
        var authorId = UUID.randomUUID();
        when(repository.existsById(authorId)).thenReturn(false);

        var result = service.deleteById(authorId);

        assertThat(result).isFalse();

        verify(repository).existsById(authorId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

}
