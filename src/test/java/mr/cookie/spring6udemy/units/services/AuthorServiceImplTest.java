package mr.cookie.spring6udemy.units.services;

import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import mr.cookie.spring6udemy.model.mappers.AuthorMapper;
import mr.cookie.spring6udemy.model.mappers.AuthorMapperImpl;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import mr.cookie.spring6udemy.services.AuthorServiceImpl;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    private static final UUID AUTHOR_ID = UUID.randomUUID();
    private static final AuthorDto AUTHOR_DTO = AuthorDto.builder()
            .id(AUTHOR_ID)
            .build();
    private static final Supplier<AuthorEntity> AUTHOR_ENTITY_SUPPLIER = () -> AuthorEntity.builder()
            .id(AUTHOR_ID)
            .build();

    @Spy
    @NotNull
    private AuthorMapper authorMapper = new AuthorMapperImpl();

    @Mock
    @NotNull
    private AuthorRepository authorRepository;

    @NotNull
    @InjectMocks
    private AuthorServiceImpl authorService;

    @Captor
    private ArgumentCaptor<AuthorEntity> authorDtoArgumentCaptor;

    @Test
    void shouldReturnAllAuthors() {
        var authorEntity = AUTHOR_ENTITY_SUPPLIER.get();

        when(this.authorRepository.findAll())
                .thenReturn(Collections.singletonList(authorEntity));

        var result = this.authorService.findAll();

        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .contains(AuthorDto.builder().id(AUTHOR_ID).build());

        verify(this.authorRepository).findAll();
        verify(this.authorMapper).mapToModel(anyIterable());
        verify(this.authorMapper).map(authorEntity);
        verifyNoMoreInteractions(this.authorRepository, this.authorMapper);
    }

    @Test
    void shouldReturnAuthorById() {
        var authorEntity = AUTHOR_ENTITY_SUPPLIER.get();

        when(this.authorRepository.findById(any(UUID.class))).thenReturn(Optional.of(authorEntity));

        var result = this.authorService.findById(AUTHOR_ID);

        assertThat(result)
                .isNotNull()
                .isPresent()
                .get()
                .returns(AUTHOR_ID, AuthorDto::getId);

        verify(this.authorRepository).findById(AUTHOR_ID);
        verify(this.authorMapper).map(authorEntity);
        verifyNoMoreInteractions(this.authorRepository, this.authorMapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotFindAuthorById() {
        when(this.authorRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        var result = this.authorService.findById(AUTHOR_ID);

        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(this.authorRepository).findById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorRepository);
        verifyNoInteractions(this.authorMapper);
    }

    @Test
    void shouldCreateNewAuthor() {
        var authorEntity = AUTHOR_ENTITY_SUPPLIER.get();

        when(this.authorRepository.save(any(AuthorEntity.class))).thenReturn(authorEntity);

        var result = this.authorService.create(AUTHOR_DTO);

        assertThat(result)
                .isNotNull()
                .returns(AUTHOR_ID, AuthorDto::getId);

        verify(this.authorRepository).save(authorEntity);
        verify(this.authorMapper).map(authorEntity);
        verify(this.authorMapper).map(AUTHOR_DTO);
        verifyNoMoreInteractions(this.authorRepository, this.authorMapper);
    }

    @Test
    void shouldUpdateExistingAuthor() {
        var authorEntity = AUTHOR_ENTITY_SUPPLIER.get();
        var updatedAuthorDto = AuthorDto.builder()
                .firstName("Brandon")
                .lastName("Sanderson")
                .build();

        when(this.authorRepository.findById(any(UUID.class))).thenReturn(Optional.of(authorEntity));
        when(this.authorRepository.save(any(AuthorEntity.class))).thenReturn(authorEntity);

        var result = this.authorService.update(AUTHOR_ID, updatedAuthorDto);

        assertThat(result)
                .isNotNull()
                .returns(AUTHOR_ID, AuthorDto::getId)
                .returns(updatedAuthorDto.getFirstName(), AuthorDto::getFirstName)
                .returns(updatedAuthorDto.getLastName(), AuthorDto::getLastName);

        verify(this.authorRepository).findById(AUTHOR_ID);
        verify(this.authorRepository).save(this.authorDtoArgumentCaptor.capture());
        verify(this.authorMapper).map(authorEntity);
        verifyNoMoreInteractions(this.authorRepository, this.authorMapper);

        assertThat(this.authorDtoArgumentCaptor.getValue())
                .isNotNull()
                .returns(AUTHOR_ID, AuthorEntity::getId)
                .returns(updatedAuthorDto.getFirstName(), AuthorEntity::getFirstName)
                .returns(updatedAuthorDto.getLastName(), AuthorEntity::getLastName);
    }

    @Test
    void shouldThrowExceptionWhenCannotUpdateAuthorById() {
        when(this.authorRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.authorService.update(AUTHOR_ID, AUTHOR_DTO))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(this.authorRepository).findById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorRepository);
        verifyNoInteractions(this.authorMapper);
    }

    @Test
    void shouldDeleteExistingAuthor() {
        when(this.authorRepository.findById(any(UUID.class))).thenReturn(Optional.of(AUTHOR_ENTITY_SUPPLIER.get()));

        this.authorService.deleteById(AUTHOR_ID);

        verify(this.authorRepository).deleteById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorRepository);
        verifyNoInteractions(this.authorMapper);
    }

    @Test
    void shouldThrowExceptionWhenCannotDeleteAuthorById() {
        when(this.authorRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.authorService.deleteById(AUTHOR_ID))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(NotFoundEntityException.ERROR_MESSAGE, AuthorDto.class.getSimpleName(), AUTHOR_ID);

        verify(this.authorRepository).findById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorRepository);
        verifyNoInteractions(this.authorMapper);
    }

}
