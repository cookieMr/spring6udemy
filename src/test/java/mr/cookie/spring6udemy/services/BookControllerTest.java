package mr.cookie.spring6udemy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.model.model.Book;
import org.hamcrest.core.Is;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    private static final long BOOK_ID = 1L;
    private static final Supplier<Book> BOOK_SUPPLIER = () -> Book.builder()
            .title("Warbreaker")
            .isbn("978-0765360038")
            .build();

    @NotNull
    @Autowired
    private ObjectMapper objectMapper;

    @NotNull
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetAllBooks() {
        var books = this.getAllBooks();

        assertThat(books)
                .isNotNull()
                .isNotEmpty();
        // TODO: should contain a book
    }

    @Test
    void shouldCreateAndThenGetBookById() {
        var book = BOOK_SUPPLIER.get();

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
    void shouldCreateBook() {
        var book = BOOK_SUPPLIER.get();

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

    @Test
    void shouldUpdateBook() {
        var book = BOOK_SUPPLIER.get();

        var result = this.updateBook(BOOK_ID, book);

        assertThat(result).isNotNull();
        assertThat(result)
                .returns(BOOK_ID, Book::getId)
                .returns(book.getTitle(), Book::getTitle)
                .returns(book.getIsbn(), Book::getIsbn);
    }

    @Test
    void shouldDeleteExistingBook() {
        var book = BOOK_SUPPLIER.get();

        var bookId = this.createBook(book).getId();
        this.deleteBookById(bookId);
    }

    @SneakyThrows
    @NotNull
    private List<Book> getAllBooks() {
        var strBooks = this.mockMvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var arrayBooks = this.objectMapper.readValue(strBooks, Book[].class);
        return Arrays.asList(arrayBooks);
    }

    @SneakyThrows
    @NotNull
    private Book createBook(@NotNull Book book) {
        var strBook = this.mockMvc.perform(post("/book")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(book))
                )
                .andExpect(status().isCreated())
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

    @SneakyThrows
    @NotNull
    private Book updateBook(long bookId, @NotNull Book book) {
        var strBook = this.mockMvc.perform(put("/book/{id}", bookId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(book))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id", Is.is(bookId), Long.class))
                .andExpect(jsonPath("$.title").value(book.getTitle()))
                .andExpect(jsonPath("$.isbn").value(book.getIsbn()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strBook, Book.class);
    }

    @SneakyThrows
    private void deleteBookById(long bookId) {
        this.mockMvc.perform(delete("/book/{id}", bookId))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

}
