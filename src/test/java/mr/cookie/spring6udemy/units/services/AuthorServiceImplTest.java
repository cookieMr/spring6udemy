package mr.cookie.spring6udemy.units.services;

import mr.cookie.spring6udemy.model.entities.AuthorDto;
import mr.cookie.spring6udemy.model.mappers.AuthorMapper;
import mr.cookie.spring6udemy.model.mappers.AuthorMapperImpl;
import mr.cookie.spring6udemy.model.model.Author;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import mr.cookie.spring6udemy.services.AuthorServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    private static final long AUTHOR_ID = 2L;

    @Spy
    @NotNull
    private AuthorMapper authorMapper = new AuthorMapperImpl();

    @Mock
    @NotNull
    private AuthorRepository authorRepository;

    @NotNull
    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    void shouldReturnAllAuthors() {
        var authorDto = AuthorDto.builder().id(AUTHOR_ID).build();

        when(this.authorRepository.findAll())
                .thenReturn(Collections.singletonList(authorDto));

        var result = this.authorService.findAll();

        assertThat(result)
                .isNotNull()
                .hasSize(1)
                .containsOnly(Author.builder().id(AUTHOR_ID).build());

        verify(this.authorRepository).findAll();
        verify(this.authorMapper).mapToModel(anyIterable());
        verify(this.authorMapper).map(authorDto);
        verifyNoMoreInteractions(this.authorRepository, this.authorMapper);
    }

    @Test
    void shouldReturnAuthorById() {
        var authorDto = AuthorDto.builder().id(AUTHOR_ID).build();

        when(this.authorRepository.findById(anyLong())).thenReturn(Optional.of(authorDto));

        var result = this.authorService.findById(AUTHOR_ID);

        assertThat(result)
                .isNotNull()
                .returns(AUTHOR_ID, Author::getId);

        verify(this.authorRepository).findById(AUTHOR_ID);
        verify(this.authorMapper).map(authorDto);
        verifyNoMoreInteractions(this.authorRepository, this.authorMapper);
    }

}
