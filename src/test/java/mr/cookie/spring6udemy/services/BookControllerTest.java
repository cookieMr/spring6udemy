package mr.cookie.spring6udemy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static mr.cookie.spring6udemy.services.constants.Constants.BLANK_STRING;
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
                .matches(dto -> bookId.equals(dto.getId()))
                .returns(bookDto.getTitle(), BookDto::getTitle)
                .returns(bookDto.getIsbn(), BookDto::getIsbn);
    }

    static Stream<Consumer<BookDto>> bookModifiers() {
        return Stream.of(
                book -> book.setTitle(null),
                book -> book.setTitle(BLANK_STRING),
                book -> book.setTitle(RandomStringUtils.random(129)),
                book -> book.setIsbn(null),
                book -> book.setIsbn(BLANK_STRING),
                book -> book.setIsbn(RandomStringUtils.random(12)),
                book -> book.setIsbn(RandomStringUtils.random(15))
        );
    }

    @ParameterizedTest
    @MethodSource("bookModifiers")
    void shouldFailToCreateBook(@NotNull Consumer<BookDto> bookModifier) {
        var bookDto = BOOK_DTO_SUPPLIER.get();
        bookModifier.accept(bookDto);

        this.createBookAndExpect400(bookDto);
    }

    @Test
    void shouldReturn404WhenBookIsNotFound() {
        var bookId = UUID.randomUUID();
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
        );
    }

    @Test
    void shouldUpdateBook() {
        var bookDto = BOOK_DTO_SUPPLIER.get();
        var createdBook = this.createBook(bookDto);

        var result = this.updateBook(createdBook);

        assertThat(result)
                .isNotNull()
                .matches(dto -> dto.getId().equals(createdBook.getId()))
                .returns(bookDto.getTitle(), BookDto::getTitle)
                .returns(bookDto.getIsbn(), BookDto::getIsbn);
    }

    @ParameterizedTest
    @MethodSource("bookModifiers")
    void shouldFailToUpdateBook(@NotNull Consumer<BookDto> bookModifier) {
        var bookDto = BOOK_DTO_SUPPLIER.get();
        var createdBook = this.createBook(bookDto);
        bookModifier.accept(createdBook);

        this.updateBookAndExpect400(createdBook);
    }

    @Test
    void shouldReturn404WhenUpdatingBookIsNotFound() {
        var bookDto = BOOK_DTO_SUPPLIER.get();
        var bookId = UUID.randomUUID();
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
        var bookId = UUID.randomUUID();
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
    private BookDto createBook(@NotNull BookDto bookDto) {
        var strBook = this.mockMvc.perform(post("/book")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(bookDto))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(bookDto.getTitle()))
                .andExpect(jsonPath("$.isbn").value(bookDto.getIsbn()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strBook, BookDto.class);
    }

    @SneakyThrows
    private void createBookAndExpect400(@NotNull BookDto bookDto) {
        this.mockMvc.perform(post("/book")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(bookDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @NotNull
    private BookDto getBookById(@NotNull UUID bookId) {
        var strBook = this.mockMvc.perform(get("/book/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(bookId.toString()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strBook, BookDto.class);
    }

    @SneakyThrows
    private void getBookByIdAndExpect404(@NotNull UUID bookId) {
        this.mockMvc.perform(get("/book/{id}", bookId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @NotNull
    private BookDto updateBook(@NotNull BookDto bookDto) {
        var strBook = this.mockMvc.perform(put("/book/{id}", bookDto.getId())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(bookDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(bookDto.getId().toString()))
                .andExpect(jsonPath("$.title").value(bookDto.getTitle()))
                .andExpect(jsonPath("$.isbn").value(bookDto.getIsbn()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strBook, BookDto.class);
    }

    @SneakyThrows
    private void updateBookAndExpect400(@NotNull BookDto bookDto) {
        this.mockMvc.perform(put("/book/{id}", bookDto.getId())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(bookDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    private void updateBookAndExpect404(@NotNull UUID bookId, @NotNull BookDto bookDto) {
        this.mockMvc.perform(put("/book/{id}", bookId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(bookDto))
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    private void deleteBookById(@NotNull UUID bookId) {
        this.mockMvc.perform(delete("/book/{id}", bookId))
                .andExpect(status().isNoContent());
    }

    @SneakyThrows
    private void deleteBookAndExpect404(@NotNull UUID bookId) {
        this.mockMvc.perform(delete("/book/{id}", bookId))
                .andExpect(status().isNotFound());
    }

}
