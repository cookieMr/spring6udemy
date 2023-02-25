package mr.cookie.spring6udemy.services;

import mr.cookie.spring6udemy.model.model.Book;
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
class BookControllerTest {

    private static final long BOOK_ID = 1L;
    private static final Book EXPECTED_BOOK = Book.builder()
            .id(BOOK_ID)
            .title("Way of Kings")
            .isbn("978-0765365279")
            .build();

    @NotNull
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllBooks() throws Exception {
        this.mockMvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].title").value(EXPECTED_BOOK.getTitle()))
                .andExpect(jsonPath("$[0].isbn").value(EXPECTED_BOOK.getIsbn()));
    }

    @Test
    void getAuthorById() throws Exception {
        this.mockMvc.perform(get("/book/%s".formatted(BOOK_ID)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(EXPECTED_BOOK.getTitle()))
                .andExpect(jsonPath("$.isbn").value(EXPECTED_BOOK.getIsbn()));
    }

    @Disabled
    @Test
    void shouldReturn404WhenBookIsNotFound() throws Exception {
        // TODO: error handling
        this.mockMvc.perform(get("/book/%s".formatted(Integer.MAX_VALUE)))
                .andExpect(status().isNotFound());
    }

}
