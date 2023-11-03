package mr.cookie.spring6udemy.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
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

    private static final UUID AUTHOR_ID = UUID.randomUUID();
    private static final AuthorDto AUTHOR_DTO = AuthorDto.builder()
            .id(AUTHOR_ID)
            .build();
    private static final Supplier<AuthorEntity> AUTHOR_ENTITY_SUPPLIER = () -> AuthorEntity.builder()
            .id(AUTHOR_ID)
            .build();

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
        var authorEntity = AUTHOR_ENTITY_SUPPLIER.get();

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
        var authorEntity = AUTHOR_ENTITY_SUPPLIER.get();

        when(authorRepository.findById(AUTHOR_ID))
                .thenReturn(Optional.of(authorEntity));

        var result = authorService.findById(AUTHOR_ID);

        assertThat(result)
                .isNotNull()
                .isPresent()
                .get()
                .returns(AUTHOR_ID, AuthorDto::getId);

        verify(authorRepository).findById(AUTHOR_ID);
        verify(authorMapper).map(authorEntity);
        verifyNoMoreInteractions(authorRepository, authorMapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        when(authorRepository.findById(AUTHOR_ID)).thenReturn(Optional.empty());

        var result = authorService.findById(AUTHOR_ID);

        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(authorRepository).findById(AUTHOR_ID);
        verifyNoMoreInteractions(authorRepository);
        verifyNoInteractions(authorMapper);
    }

    @Test
    void shouldCreateNewAuthor() {
        var authorEntity = AUTHOR_ENTITY_SUPPLIER.get();

        when(authorRepository.save(authorEntity)).thenReturn(authorEntity);

        var result = authorService.create(AUTHOR_DTO);

        assertThat(result)
                .isNotNull()
                .returns(AUTHOR_ID, AuthorDto::getId);

        verify(authorRepository).save(authorEntity);
        verify(authorMapper).map(authorEntity);
        verify(authorMapper).map(AUTHOR_DTO);
        verifyNoMoreInteractions(authorRepository, authorMapper);
    }

    @Test
    void shouldUpdateExistingAuthor() {
        var authorEntity = AUTHOR_ENTITY_SUPPLIER.get();
        var updatedAuthorDto = AuthorDto.builder()
                .firstName("Brandon")
                .lastName("Sanderson")
                .build();

        when(authorRepository.findById(AUTHOR_ID)).thenReturn(Optional.of(authorEntity));
        when(authorRepository.save(authorEntity)).thenReturn(authorEntity);

        var result = authorService.update(AUTHOR_ID, updatedAuthorDto);

        assertThat(result)
                .isNotNull()
                .returns(AUTHOR_ID, AuthorDto::getId)
                .returns(updatedAuthorDto.getFirstName(), AuthorDto::getFirstName)
                .returns(updatedAuthorDto.getLastName(), AuthorDto::getLastName);

        verify(authorRepository).findById(AUTHOR_ID);
        verify(authorRepository).save(authorDtoArgumentCaptor.capture());
        verify(authorMapper).map(authorEntity);
        verifyNoMoreInteractions(authorRepository, authorMapper);

        assertThat(authorDtoArgumentCaptor.getValue())
                .isNotNull()
                .returns(AUTHOR_ID, AuthorEntity::getId)
                .returns(updatedAuthorDto.getFirstName(), AuthorEntity::getFirstName)
                .returns(updatedAuthorDto.getLastName(), AuthorEntity::getLastName);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        when(authorRepository.findById(AUTHOR_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.update(AUTHOR_ID, AUTHOR_DTO))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(authorRepository).findById(AUTHOR_ID);
        verifyNoMoreInteractions(authorRepository);
        verifyNoInteractions(authorMapper);
    }

    @Test
    void shouldDeleteExistingAuthor() {
        when(authorRepository.existsById(AUTHOR_ID)).thenReturn(true);

        var result = authorService.deleteById(AUTHOR_ID);

        assertThat(result).isTrue();

        verify(authorRepository).deleteById(AUTHOR_ID);
        verify(authorRepository).existsById(AUTHOR_ID);
        verifyNoMoreInteractions(authorRepository);
        verifyNoInteractions(authorMapper);
    }

    @Test
    void shouldNotDeleteNotExistingAuthor() {
        when(authorRepository.existsById(AUTHOR_ID)).thenReturn(false);

        var result = authorService.deleteById(AUTHOR_ID);

        assertThat(result).isFalse();

        verify(authorRepository).existsById(AUTHOR_ID);
        verifyNoMoreInteractions(authorRepository);
        verifyNoInteractions(authorMapper);
    }

}
