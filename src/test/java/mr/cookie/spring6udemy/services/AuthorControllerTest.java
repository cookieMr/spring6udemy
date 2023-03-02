package mr.cookie.spring6udemy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
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
    private static final Supplier<AuthorDto> AUTHOR_SUPPLIER = () -> AuthorDto.builder()
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
                .returns(authorId, AuthorDto::getId)
                .returns(author.getFirstName(), AuthorDto::getFirstName)
                .returns(author.getLastName(), AuthorDto::getLastName);
    }

    @Test
    void shouldReturn404WhenAuthorIsNotFound() {
        var authorId = Integer.MAX_VALUE;
        this.getAuthorByIdAndExpect404(authorId);
    }

    @Test
    void shouldCreateAuthor() {
        var author = AUTHOR_SUPPLIER.get();

        var result = this.createAuthor(author);

        assertThat(result).isNotNull();
        assertAll(
                () -> assertThat(result)
                        .returns(author.getFirstName(), AuthorDto::getFirstName)
                        .returns(author.getLastName(), AuthorDto::getLastName),
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
                .returns(AUTHOR_ID, AuthorDto::getId)
                .returns(author.getFirstName(), AuthorDto::getFirstName)
                .returns(author.getLastName(), AuthorDto::getLastName);
    }

    @Test
    void shouldReturn404WhenUpdatingAuthorIsNotFound() {
        var author = AUTHOR_SUPPLIER.get();
        var authorId = Integer.MAX_VALUE;
        this.updateAuthorAndExpect404(authorId, author);
    }

    @Test
    void shouldDeleteExistingAuthor() {
        var author = AUTHOR_SUPPLIER.get();

        var authorId = this.createAuthor(author).getId();
        this.deleteAuthorById(authorId);
    }

    @Test
    void shouldReturn404WhenDeletingAuthorIsNotFound() {
        var authorId = Integer.MAX_VALUE;
        this.deleteAuthorAndExpect404(authorId);
    }

    @SneakyThrows
    @NotNull
    private List<AuthorDto> getAllAuthors() {
        var strAuthors = this.mockMvc.perform(get("/author"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var arrayAuthors = this.objectMapper.readValue(strAuthors, AuthorDto[].class);
        return Arrays.asList(arrayAuthors);
    }

    @SneakyThrows
    @NotNull
    private AuthorDto createAuthor(@NotNull AuthorDto author) {
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

        return this.objectMapper.readValue(strAuthor, AuthorDto.class);
    }

    @SneakyThrows
    @NotNull
    private AuthorDto getAuthorById(long authorId) {
        var strAuthor = this.mockMvc.perform(get("/author/{id}", authorId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(authorId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strAuthor, AuthorDto.class);
    }

    @SneakyThrows
    private void getAuthorByIdAndExpect404(long authorId) {
        this.mockMvc.perform(get("/author/{id}", authorId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @NotNull
    private AuthorDto updateAuthor(long authorId, @NotNull AuthorDto author) {
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

        return this.objectMapper.readValue(strAuthor, AuthorDto.class);
    }

    @SneakyThrows
    private void updateAuthorAndExpect404(long authorId, @NotNull AuthorDto author) {
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
