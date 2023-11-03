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
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
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
    private ArgumentCaptor<AuthorEntity> authorDtoArgumentCaptor;

    @Spy
    private AuthorMapper authorMapper = new AuthorMapperImpl();

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    void shouldReturnAllAuthors() {
        var authorEntity = AuthorEntity.builder()
                .id(UUID.randomUUID())
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();

        when(authorRepository.findAll())
                .thenReturn(List.of(authorEntity));

        var result = authorService.findAll();

        assertThat(result)
                .isNotNull()
                .hasSize(1);

        verify(authorRepository).findAll();
        verify(authorMapper).map(authorEntity);
        verifyNoMoreInteractions(authorRepository, authorMapper);
    }

    @Test
    void shouldReturnAuthorById() {
        var authorId = UUID.randomUUID();
        var authorEntity = AuthorEntity.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .id(authorId)
                .build();

        when(authorRepository.findById(authorId))
                .thenReturn(Optional.of(authorEntity));

        var result = authorService.findById(authorId);

        assertThat(result)
                .isNotNull()
                .isPresent()
                .get()
                .returns(authorId, AuthorDto::getId);

        verify(authorRepository).findById(authorId);
        verify(authorMapper).map(authorEntity);
        verifyNoMoreInteractions(authorRepository, authorMapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        var authorId = UUID.randomUUID();
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        var result = authorService.findById(authorId);

        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(authorRepository).findById(authorId);
        verifyNoMoreInteractions(authorRepository);
        verifyNoInteractions(authorMapper);
    }

    @Test
    void shouldCreateNewAuthor() {
        var authorId = UUID.randomUUID();
        var authorEntity = AuthorEntity.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .id(authorId)
                .build();

        when(authorRepository.save(authorEntity)).thenReturn(authorEntity);

        var authorDto = AuthorDto.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .id(authorId)
                .build();
        var result = authorService.create(authorDto);

        assertThat(result)
                .isNotNull()
                .returns(authorId, AuthorDto::getId);

        verify(authorRepository).save(authorEntity);
        verify(authorMapper).map(authorEntity);
        verify(authorMapper).map(authorDto);
        verifyNoMoreInteractions(authorRepository, authorMapper);
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

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(authorEntity));
        when(authorRepository.save(authorEntity)).thenReturn(authorEntity);

        var result = authorService.update(authorId, updatedAuthorDto);

        assertThat(result)
                .isNotNull()
                .returns(authorId, AuthorDto::getId)
                .returns(updatedAuthorDto.getFirstName(), AuthorDto::getFirstName)
                .returns(updatedAuthorDto.getLastName(), AuthorDto::getLastName);

        verify(authorRepository).findById(authorId);
        verify(authorRepository).save(authorDtoArgumentCaptor.capture());
        verify(authorMapper).map(authorEntity);
        verifyNoMoreInteractions(authorRepository, authorMapper);

        assertThat(authorDtoArgumentCaptor.getValue())
                .isNotNull()
                .returns(authorId, AuthorEntity::getId)
                .returns(updatedAuthorDto.getFirstName(), AuthorEntity::getFirstName)
                .returns(updatedAuthorDto.getLastName(), AuthorEntity::getLastName);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        var authorId = UUID.randomUUID();
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        var authorDto = AuthorDto.builder()
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();

        assertThatThrownBy(() -> authorService.update(authorId, authorDto))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(authorRepository).findById(authorId);
        verifyNoMoreInteractions(authorRepository);
        verifyNoInteractions(authorMapper);
    }

    @Test
    void shouldDeleteExistingAuthor() {
        var authorId = UUID.randomUUID();
        when(authorRepository.existsById(authorId)).thenReturn(true);

        var result = authorService.deleteById(authorId);

        assertThat(result).isTrue();

        verify(authorRepository).deleteById(authorId);
        verify(authorRepository).existsById(authorId);
        verifyNoMoreInteractions(authorRepository);
        verifyNoInteractions(authorMapper);
    }

    @Test
    void shouldNotDeleteNotExistingAuthor() {
        var authorId = UUID.randomUUID();
        when(authorRepository.existsById(authorId)).thenReturn(false);

        var result = authorService.deleteById(authorId);

        assertThat(result).isFalse();

        verify(authorRepository).existsById(authorId);
        verifyNoMoreInteractions(authorRepository);
        verifyNoInteractions(authorMapper);
    }

}
