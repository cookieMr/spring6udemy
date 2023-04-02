package mr.cookie.spring6udemy.units.services;

import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import mr.cookie.spring6udemy.model.mappers.AuthorMapper;
import mr.cookie.spring6udemy.model.mappers.AuthorMapperImpl;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import mr.cookie.spring6udemy.services.AuthorServiceImpl;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
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

    @Captor
    private ArgumentCaptor<AuthorEntity> authorDtoArgumentCaptor;

    private AuthorMapper authorMapper;
    private AuthorRepository authorRepository;
    private AuthorServiceImpl authorService;

    @BeforeEach
    void setupBeforeAll() {
        this.authorRepository = mock(AuthorRepository.class);
        this.authorMapper = spy(new AuthorMapperImpl());
        this.authorService = new AuthorServiceImpl(
                25,
                this.authorMapper,
                this.authorRepository
        );
    }

    @AfterEach
    void cleanUp() {
        reset(this.authorRepository, this.authorMapper);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -42})
    void shouldFailInitializationWithInvalidPageSize(int invalidPageSize) {
        assertThatThrownBy(() -> new AuthorServiceImpl(invalidPageSize, this.authorMapper, this.authorRepository))
                .isNotNull()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The default pageSize should be greater than zero.");
    }

    @Test
    void shouldReturnAllAuthors() {
        var authorEntity = AUTHOR_ENTITY_SUPPLIER.get();
        var authorPage = new PageImpl<>(List.of(authorEntity));

        when(this.authorRepository.findAll(any(Pageable.class)))
                .thenReturn(authorPage);

        var result = this.authorService.findAll(null, null);

        assertThat(result)
                .isNotNull()
                .hasSize(1);

        verify(this.authorRepository).findAll(any(PageRequest.class));
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
        when(this.authorRepository.existsById(any(UUID.class))).thenReturn(true);

        var result = this.authorService.deleteById(AUTHOR_ID);

        assertThat(result).isTrue();

        verify(this.authorRepository).deleteById(AUTHOR_ID);
        verify(this.authorRepository).existsById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorRepository);
        verifyNoInteractions(this.authorMapper);
    }

    @Test
    void shouldNotDeleteNotExistingAuthor() {
        when(this.authorRepository.existsById(any(UUID.class))).thenReturn(false);

        var result = this.authorService.deleteById(AUTHOR_ID);

        assertThat(result).isFalse();

        verify(this.authorRepository).existsById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorRepository);
        verifyNoInteractions(this.authorMapper);
    }

}
