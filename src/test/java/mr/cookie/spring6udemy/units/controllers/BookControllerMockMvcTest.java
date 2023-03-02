package mr.cookie.spring6udemy.units.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.controllers.BookController;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.services.BookService;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
@WebMvcTest(BookController.class)
class BookControllerMockMvcTest {

    private static final UUID BOOK_ID = UUID.randomUUID();
    private static final BookDto BOOK_DTO = BookDto.builder()
            .title("Warbreaker")
            .isbn("978-0765360038")
            .build();

    @NotNull
    @Autowired
    private ObjectMapper objectMapper;

    @NotNull
    @Autowired
    private MockMvc mockMvc;

    @NotNull
    @MockBean
    private BookService bookService;

    @Test
    void shouldGetAllBooks() {
        given(this.bookService.findAll()).willReturn(Collections.singletonList(BOOK_DTO));

        var result = this.getAllBooks();

        assertThat(result)
                .isNotNull()
                .containsOnly(BOOK_DTO);

        verify(this.bookService).findAll();
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldGetBookById() {
        given(this.bookService.findById(any(UUID.class))).willReturn(Optional.of(BOOK_DTO));

        var result = this.getBookById(BOOK_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(BOOK_DTO);

        verify(this.bookService).findById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldGet404WhenCannotFindBookById() {
        given(this.bookService.findById(any(UUID.class)))
                .willThrow(new NotFoundEntityException(BOOK_ID, BookDto.class));

        this.getBookByIdAndExpect404(BOOK_ID);

        verify(this.bookService).findById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldCreateBook() {
        given(this.bookService.create(any(BookDto.class))).willReturn(BOOK_DTO);

        var result = this.createBook(BOOK_DTO);

        assertThat(result)
                .isNotNull()
                .isEqualTo(BOOK_DTO);

        verify(this.bookService).create(BOOK_DTO);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldUpdateBook() {
        given(this.bookService.update(any(UUID.class), any(BookDto.class))).willReturn(BOOK_DTO);

        var result = this.updateBook(BOOK_ID, BOOK_DTO);

        assertThat(result)
                .isNotNull()
                .isEqualTo(BOOK_DTO);

        verify(this.bookService).update(BOOK_ID, BOOK_DTO);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldGet404WhenCannotUpdateBookById() {
        given(this.bookService.update(any(UUID.class), any(BookDto.class)))
                .willThrow(new NotFoundEntityException(BOOK_ID, BookDto.class));

        this.updateBookAndExpect404(BOOK_ID, BOOK_DTO);

        verify(this.bookService).update(BOOK_ID, BOOK_DTO);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldDeleteBook() {
        this.deleteBookById(BOOK_ID);

        verify(this.bookService).deleteById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldGet404WhenCannotDeleteBookById() {
        doThrow(new NotFoundEntityException(BOOK_ID, BookDto.class))
                .when(this.bookService)
                .deleteById(BOOK_ID);

        this.deleteBookAndExpect404(BOOK_ID);

        verify(this.bookService).deleteById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

    @SneakyThrows
    @NotNull
    private List<BookDto> getAllBooks() {
        var strBooks = this.mockMvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", Is.is(1)))
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
                .andExpect(jsonPath("$.title").value(book.getTitle()))
                .andExpect(jsonPath("$.isbn").value(book.getIsbn()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strBook, BookDto.class);
    }

    @SneakyThrows
    @NotNull
    private BookDto getBookById(@NotNull UUID bookId) {
        var strBook = this.mockMvc.perform(get("/book/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
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
    private BookDto updateBook(@NotNull UUID bookId, @NotNull BookDto book) {
        var strBook = this.mockMvc.perform(put("/book/{id}", bookId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(book))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(book.getTitle()))
                .andExpect(jsonPath("$.isbn").value(book.getIsbn()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strBook, BookDto.class);
    }

    @SneakyThrows
    private void updateBookAndExpect404(@NotNull UUID bookId, @NotNull BookDto book) {
        this.mockMvc.perform(put("/book/{id}", bookId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(book))
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
