package mr.cookie.spring6udemy.services;

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
    void setupBeforeEach() {
        authorRepository = mock(AuthorRepository.class);
        authorMapper = spy(new AuthorMapperImpl());
        authorService = new AuthorServiceImpl(25, authorMapper, authorRepository);
    }

    @AfterEach
    void cleanUp() {
        reset(authorRepository, authorMapper);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -42})
    void shouldFailInitializationWithInvalidPageSize(int invalidPageSize) {
        assertThatThrownBy(() -> new AuthorServiceImpl(invalidPageSize, authorMapper, authorRepository))
                .isNotNull()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The default pageSize should be greater than zero.");
    }

    @Test
    void shouldReturnAllAuthors() {
        var authorEntity = AUTHOR_ENTITY_SUPPLIER.get();
        var authorPage = new PageImpl<>(List.of(authorEntity));

        when(authorRepository.findAll(any(Pageable.class)))
                .thenReturn(authorPage);

        var result = authorService.findAll(null, null);

        assertThat(result)
                .isNotNull()
                .hasSize(1);

        verify(authorRepository).findAll(any(PageRequest.class));
        verify(authorMapper).map(authorEntity);
        verifyNoMoreInteractions(authorRepository, authorMapper);
    }

    @Test
    void shouldReturnAuthorById() {
        var authorEntity = AUTHOR_ENTITY_SUPPLIER.get();

        when(authorRepository.findById(any(UUID.class)))
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
        when(authorRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

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

        when(authorRepository.save(any(AuthorEntity.class))).thenReturn(authorEntity);

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

        when(authorRepository.findById(any(UUID.class))).thenReturn(Optional.of(authorEntity));
        when(authorRepository.save(any(AuthorEntity.class))).thenReturn(authorEntity);

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
        when(authorRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.update(AUTHOR_ID, AUTHOR_DTO))
                .isNotNull()
                .isInstanceOf(NotFoundEntityException.class);

        verify(authorRepository).findById(AUTHOR_ID);
        verifyNoMoreInteractions(authorRepository);
        verifyNoInteractions(authorMapper);
    }

    @Test
    void shouldDeleteExistingAuthor() {
        when(authorRepository.existsById(any(UUID.class))).thenReturn(true);

        var result = authorService.deleteById(AUTHOR_ID);

        assertThat(result).isTrue();

        verify(authorRepository).deleteById(AUTHOR_ID);
        verify(authorRepository).existsById(AUTHOR_ID);
        verifyNoMoreInteractions(authorRepository);
        verifyNoInteractions(authorMapper);
    }

    @Test
    void shouldNotDeleteNotExistingAuthor() {
        when(authorRepository.existsById(any(UUID.class))).thenReturn(false);

        var result = authorService.deleteById(AUTHOR_ID);

        assertThat(result).isFalse();

        verify(authorRepository).existsById(AUTHOR_ID);
        verifyNoMoreInteractions(authorRepository);
        verifyNoInteractions(authorMapper);
    }

}
