package mr.cookie.spring6udemy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.model.model.Author;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorControllerTest {

    private static final long AUTHOR_ID = 1L;
    private static final Author EXPECTED_AUTHOR = Author.builder()
            .id(AUTHOR_ID)
            .firstName("Brandon")
            .lastName("Sanderson")
            .build();

    @NotNull
    @Autowired
    private ObjectMapper objectMapper;

    @NotNull
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllAuthors() throws Exception {
        this.mockMvc.perform(get("/author"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value(EXPECTED_AUTHOR.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(EXPECTED_AUTHOR.getLastName()));
    }

    @Test
    void getAuthorById() {
        var author = Author.builder()
                .firstName("JRR")
                .lastName("Tolkien")
                .build();

        var authorId = this.createAuthor(author).getId();
        var result = this.getAuthorById(authorId);

        assertThat(result)
                .isNotNull()
                .returns(authorId, Author::getId)
                .returns(author.getFirstName(), Author::getFirstName)
                .returns(author.getLastName(), Author::getLastName);
    }

    @Disabled
    @Test
    void shouldReturn404WhenAuthorIsNotFound() throws Exception {
        // TODO: error handling
        this.mockMvc.perform(get("/author/{id}", Integer.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void postAuthorToCreate() {
        var author = Author.builder()
                .firstName("George")
                .lastName("RR Martin")
                .build();

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

    @SneakyThrows
    @NotNull
    private Author createAuthor(@NotNull Author author) {
        var strAuthor = this.mockMvc.perform(post("/author")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(author))
                )
                .andExpect(status().isOk())
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

}
