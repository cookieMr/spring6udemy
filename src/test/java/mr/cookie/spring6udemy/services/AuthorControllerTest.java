package mr.cookie.spring6udemy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.model.model.Author;
import mr.cookie.spring6udemy.services.exceptions.NotFoundEntityException;
import org.hamcrest.core.Is;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SameParameterValue")
@SpringBootTest
@AutoConfigureMockMvc
class AuthorControllerTest {

    private static final long AUTHOR_ID = 1L;
    private static final Supplier<Author> AUTHOR_SUPPLIER = () -> Author.builder()
            .firstName("JRR")
            .lastName("Tolkien")
            .build();

    @NotNull
    @Autowired
    private ObjectMapper objectMapper;

    @NotNull
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetAllAuthors() {
        var result = this.getAllAuthors();

        assertThat(result)
                .isNotNull()
                .isNotEmpty();
        // TODO: should contain an author
    }

    @Test
    void shouldCreateAndThenGetAuthorById() {
        var author = AUTHOR_SUPPLIER.get();

        var authorId = this.createAuthor(author).getId();
        var result = this.getAuthorById(authorId);

        assertThat(result)
                .isNotNull()
                .returns(authorId, Author::getId)
                .returns(author.getFirstName(), Author::getFirstName)
                .returns(author.getLastName(), Author::getLastName);
    }

    @Test
    void shouldReturn404WhenAuthorIsNotFound() {
        var authorId = Integer.MAX_VALUE;
        var result = this.getAuthorByIdAndExpect404(authorId);

        assertThat(result)
                .isNotNull()
                .isEqualTo(NotFoundEntityException.ERROR_MESSAGE, Author.class.getSimpleName(), authorId);
    }

    @Test
    void shouldCreateAuthor() {
        var author = AUTHOR_SUPPLIER.get();

        var result = this.createAuthor(author);

        assertThat(result).isNotNull();
        assertAll(
                () -> assertThat(result)
                        .returns(author.getFirstName(), Author::getFirstName)
                        .returns(author.getLastName(), Author::getLastName),
                () -> assertThat(result.getId())
                        .isNotNull()
                        .isPositive()
        );
    }

    @Test
    void shouldUpdateAuthor() {
        var author = AUTHOR_SUPPLIER.get();

        var result = this.updateAuthor(AUTHOR_ID, author);

        assertThat(result).isNotNull();
        assertThat(result)
                .returns(AUTHOR_ID, Author::getId)
                .returns(author.getFirstName(), Author::getFirstName)
                .returns(author.getLastName(), Author::getLastName);
    }

    @Test
    void shouldDeleteExistingAuthor() {
        var author = AUTHOR_SUPPLIER.get();

        var authorId = this.createAuthor(author).getId();
        this.deleteAuthorById(authorId);
    }

    @SneakyThrows
    @NotNull
    private List<Author> getAllAuthors() {
        var strAuthors = this.mockMvc.perform(get("/author"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
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
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
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
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(authorId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strAuthor, Author.class);
    }

    @SneakyThrows
    @NotNull
    private String getAuthorByIdAndExpect404(long authorId) {
        return this.mockMvc.perform(get("/author/{id}", authorId))
                .andExpect(status().isNotFound())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE))
                .andReturn()
                .getResponse()
                .getContentAsString();
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
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id", Is.is(authorId), Long.class))
                .andExpect(jsonPath("$.firstName").value(author.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(author.getLastName()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strAuthor, Author.class);
    }

    @SneakyThrows
    private void deleteAuthorById(long authorId) {
        this.mockMvc.perform(delete("/author/{id}", authorId))
                .andExpect(status().isNoContent());
    }

}
