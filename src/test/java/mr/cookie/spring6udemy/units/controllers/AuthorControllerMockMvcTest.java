package mr.cookie.spring6udemy.units.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.controllers.AuthorController;
import mr.cookie.spring6udemy.model.model.Author;
import mr.cookie.spring6udemy.services.AuthorService;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.hamcrest.core.Is;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SameParameterValue")
@WebMvcTest(AuthorController.class)
class AuthorControllerMockMvcTest {

    private static final long AUTHOR_ID = 1L;
    private static final Author AUTHOR = Author.builder()
            .firstName("JRR")
            .lastName("Tolkien")
            .build();

    @NotNull
    @Autowired
    private ObjectMapper objectMapper;

    @NotNull
    @Autowired
    private MockMvc mockMvc;

    @NotNull
    @MockBean
    private AuthorService authorService;

    @Test
    void shouldGetAllAuthors() {
        given(this.authorService.findAll()).willReturn(Collections.singletonList(AUTHOR));

        var authors = this.getAllAuthors();

        assertThat(authors)
                .isNotNull()
                .containsOnly(AUTHOR);

        verify(this.authorService).findAll();
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldGetAuthorById() {
        given(this.authorService.findById(anyLong())).willReturn(AUTHOR);

        var result = this.getAuthorById(AUTHOR_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(AUTHOR);

        verify(this.authorService).findById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldGet404WhenCannotFindAuthorById() {
        given(this.authorService.findById(anyLong()))
                .willThrow(new NotFoundEntityException(AUTHOR_ID, Author.class));

        this.getAuthorByIdAndExpect404(AUTHOR_ID);

        verify(this.authorService).findById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldCreateAuthor() {
        given(this.authorService.create(any(Author.class))).willReturn(AUTHOR);

        var result = this.createAuthor(AUTHOR);

        assertThat(result)
                .isNotNull()
                .isEqualTo(AUTHOR);

        verify(this.authorService).create(AUTHOR);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldUpdateAuthor() {
        given(this.authorService.update(anyLong(), any(Author.class))).willReturn(AUTHOR);

        var result = this.updateAuthor(AUTHOR_ID, AUTHOR);

        assertThat(result)
                .isNotNull()
                .isEqualTo(AUTHOR);

        verify(this.authorService).update(AUTHOR_ID, AUTHOR);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldGet404WhenCannotUpdateAuthorById() {
        given(this.authorService.update(anyLong(), any(Author.class)))
                .willThrow(new NotFoundEntityException(AUTHOR_ID, Author.class));

        this.updateAuthorAndExpect404(AUTHOR_ID, AUTHOR);

        verify(this.authorService).update(AUTHOR_ID, AUTHOR);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldDeleteAuthor() {
        this.deleteAuthorById(AUTHOR_ID);

        verify(this.authorService).deleteById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldGet404WhenCannotDeleteAuthorById() {
        doThrow(new NotFoundEntityException(AUTHOR_ID, Author.class))
                .when(this.authorService)
                .deleteById(AUTHOR_ID);

        this.deleteAuthorAndExpect404(AUTHOR_ID);

        verify(this.authorService).deleteById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

    @SneakyThrows
    @NotNull
    private List<Author> getAllAuthors() {
        var strAuthors = this.mockMvc.perform(get("/author"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", Is.is(1)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var arrayAuthors = this.objectMapper.readValue(strAuthors, Author[].class);
        return Arrays.asList(arrayAuthors);
    }

    @SneakyThrows
    @NotNull
    private Author createAuthor(@NotNull Author author) {
        var strAuthor = this.mockMvc.perform(post("/author")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(author))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.firstName").value(author.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(author.getLastName()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strAuthor, Author.class);
    }

    @SneakyThrows
    @NotNull
    private Author getAuthorById(long authorId) {
        var strAuthor = this.mockMvc.perform(get("/author/{id}", authorId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strAuthor, Author.class);
    }

    @SneakyThrows
    private void getAuthorByIdAndExpect404(long authorId) {
        this.mockMvc.perform(get("/author/{id}", authorId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @NotNull
    private Author updateAuthor(long authorId, @NotNull Author author) {
        var strAuthor = this.mockMvc.perform(put("/author/{id}", authorId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(author))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.firstName").value(author.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(author.getLastName()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strAuthor, Author.class);
    }

    @SneakyThrows
    private void updateAuthorAndExpect404(long authorId, @NotNull Author author) {
        this.mockMvc.perform(put("/author/{id}", authorId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(author))
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    private void deleteAuthorById(long authorId) {
        this.mockMvc.perform(delete("/author/{id}", authorId))
                .andExpect(status().isNoContent());
    }

    @SneakyThrows
    private void deleteAuthorAndExpect404(long authorId) {
        this.mockMvc.perform(delete("/author/{id}", authorId))
                .andExpect(status().isNotFound());
    }

}
