package mr.cookie.spring6udemy.services;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private MockMvc mockMvc;

    @Test
    void getAllAuthors() throws Exception {
        this.mockMvc.perform(get("/author"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value(EXPECTED_AUTHOR.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(EXPECTED_AUTHOR.getLastName()));
    }

    @Test
    void getAuthorById() throws Exception {
        this.mockMvc.perform(get("/author/%s".formatted(AUTHOR_ID)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.firstName").value(EXPECTED_AUTHOR.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(EXPECTED_AUTHOR.getLastName()));
    }

    @Disabled
    @Test
    void shouldReturn404WhenAuthorIsNotFound() throws Exception {
        // TODO: error handling
        this.mockMvc.perform(get("/author/%s".formatted(Integer.MAX_VALUE)))
                .andExpect(status().isNotFound());
    }

}
