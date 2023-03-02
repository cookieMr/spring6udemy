package mr.cookie.spring6udemy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.model.dtos.BookDto;
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
class BookControllerTest {

    private static final long BOOK_ID = 1L;
    private static final Supplier<BookDto> BOOK_DTO_SUPPLIER = () -> BookDto.builder()
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
        var result = this.getAllBooks();

        assertThat(result)
                .isNotNull()
                .isNotEmpty();
        // TODO: should contain a book
    }

    @Test
    void shouldCreateAndThenGetBookById() {
        var bookDto = BOOK_DTO_SUPPLIER.get();

        var bookId = this.createBook(bookDto).getId();
        var result = this.getBookById(bookId);

        assertThat(result)
                .isNotNull()
                .returns(bookId, BookDto::getId)
                .returns(bookDto.getTitle(), BookDto::getTitle)
                .returns(bookDto.getIsbn(), BookDto::getIsbn);
    }

    @Test
    void shouldReturn404WhenBookIsNotFound() {
        var bookId = Integer.MAX_VALUE;
        this.getBookByIdAndExpect404(bookId);
    }

    @Test
    void shouldCreateBook() {
        var bookDto = BOOK_DTO_SUPPLIER.get();

        var result = this.createBook(bookDto);

        assertThat(result).isNotNull();
        assertAll(
                () -> assertThat(result)
                        .returns(bookDto.getTitle(), BookDto::getTitle)
                        .returns(bookDto.getIsbn(), BookDto::getIsbn),
                () -> assertThat(result.getId())
                        .isNotNull()
                        .isPositive()
        );
    }

    @Test
    void shouldUpdateBook() {
        var bookDto = BOOK_DTO_SUPPLIER.get();

        var result = this.updateBook(BOOK_ID, bookDto);

        assertThat(result).isNotNull();
        assertThat(result)
                .returns(BOOK_ID, BookDto::getId)
                .returns(bookDto.getTitle(), BookDto::getTitle)
                .returns(bookDto.getIsbn(), BookDto::getIsbn);
    }

    @Test
    void shouldReturn404WhenUpdatingBookIsNotFound() {
        var bookDto = BOOK_DTO_SUPPLIER.get();
        var bookId = Integer.MAX_VALUE;
        this.updateBookAndExpect404(bookId, bookDto);
    }

    @Test
    void shouldDeleteExistingBook() {
        var bookDto = BOOK_DTO_SUPPLIER.get();

        var bookId = this.createBook(bookDto).getId();
        this.deleteBookById(bookId);
    }

    @Test
    void shouldReturn404WhenDeletingBookIsNotFound() {
        var bookId = Integer.MAX_VALUE;
        this.deleteBookAndExpect404(bookId);
    }

    @SneakyThrows
    @NotNull
    private List<BookDto> getAllBooks() {
        var strBooks = this.mockMvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var arrayBooks = this.objectMapper.readValue(strBooks, BookDto[].class);
        return Arrays.asList(arrayBooks);
    }

    @SneakyThrows
    @NotNull
    private BookDto createBook(@NotNull BookDto book) {
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

        return this.objectMapper.readValue(strBook, BookDto.class);
    }

    @SneakyThrows
    @NotNull
    private BookDto getBookById(long bookId) {
        var strBook = this.mockMvc.perform(get("/book/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(bookId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strBook, BookDto.class);
    }

    @SneakyThrows
    private void getBookByIdAndExpect404(long bookId) {
        this.mockMvc.perform(get("/book/{id}", bookId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @NotNull
    private BookDto updateBook(long bookId, @NotNull BookDto book) {
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

        return this.objectMapper.readValue(strBook, BookDto.class);
    }

    @SneakyThrows
    private void updateBookAndExpect404(long bookId, @NotNull BookDto book) {
        this.mockMvc.perform(put("/book/{id}", bookId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(book))
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    private void deleteBookById(long bookId) {
        this.mockMvc.perform(delete("/book/{id}", bookId))
                .andExpect(status().isNoContent());
    }

    @SneakyThrows
    private void deleteBookAndExpect404(long bookId) {
        this.mockMvc.perform(delete("/book/{id}", bookId))
                .andExpect(status().isNotFound());
    }

}
