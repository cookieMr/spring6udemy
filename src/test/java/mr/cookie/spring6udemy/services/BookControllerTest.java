package mr.cookie.spring6udemy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
class BookControllerTest {

    private static final long BOOK_ID = 1L;
    private static final Book EXPECTED_BOOK = Book.builder()
            .id(BOOK_ID)
            .title("Way of Kings")
            .isbn("978-0765365279")
            .build();

    @NotNull
    @Autowired
    private ObjectMapper objectMapper;

    @NotNull
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllBooks() throws Exception {
        this.mockMvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value(EXPECTED_BOOK.getTitle()))
                .andExpect(jsonPath("$[0].isbn").value(EXPECTED_BOOK.getIsbn()));
    }

    @Test
    void getBookById() {
        var book = Book.builder()
                .title("Warbreaker")
                .isbn("978-0765360038")
                .build();

        var bookId = this.createBook(book).getId();
        var result = this.getBookById(bookId);

        assertThat(result)
                .isNotNull()
                .returns(bookId, Book::getId)
                .returns(book.getTitle(), Book::getTitle)
                .returns(book.getIsbn(), Book::getIsbn);
    }

    @Disabled
    @Test
    void shouldReturn404WhenBookIsNotFound() throws Exception {
        // TODO: error handling
        this.mockMvc.perform(get("/book/%s".formatted(Integer.MAX_VALUE)))
                .andExpect(status().isNotFound());
    }

    @Test
    void postBookToCreate() {
        var book = Book.builder()
                .title("Warbreaker")
                .isbn("978-0765360038")
                .build();

        var result = this.createBook(book);

        assertThat(result).isNotNull();
        assertAll(
                () -> assertThat(result)
                        .returns(book.getTitle(), Book::getTitle)
                        .returns(book.getIsbn(), Book::getIsbn),
                () -> assertThat(result.getId())
                        .isNotNull()
                        .isPositive()
        );
    }

    @SneakyThrows
    @NotNull
    private Book createBook(@NotNull Book book) {
        var strBook = this.mockMvc.perform(post("/book")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(book))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value(book.getTitle()))
                .andExpect(jsonPath("$.isbn").value(book.getIsbn()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strBook, Book.class);
    }

    @SneakyThrows
    @NotNull
    private Book getBookById(long bookId) {
        var strBook = this.mockMvc.perform(get("/book/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(bookId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strBook, Book.class);
    }

}
