package mr.cookie.spring6udemy.units.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.controllers.BookController;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.services.BookService;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import mr.cookie.spring6udemy.services.utils.MvcResponseWithBookContent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
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
class BookControllerMockMvcTests {

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
        given(this.bookService.findAll(anyInt(), anyInt()))
                .willReturn(new PageImpl<>(List.of(BOOK_DTO)));

        var result = this.getAllBooks(1);

        assertThat(result)
                .isNotNull()
                .containsOnly(BOOK_DTO);

        verify(this.bookService).findAll(0, 13);
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
        given(this.bookService.deleteById(any(UUID.class))).willReturn(true);

        this.deleteBookById(BOOK_ID);

        verify(this.bookService).deleteById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldGet404WhenCannotDeleteBookById() {
        given(this.bookService.deleteById(any(UUID.class))).willReturn(false);

        this.deleteBookAndExpect404(BOOK_ID);

        verify(this.bookService).deleteById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

    @SneakyThrows
    @NotNull
    private List<BookDto> getAllBooks(int expectedSize) {
        var mockMvcResult = this.mockMvc.perform(get("/book")
                        .param("pageNumber", "0")
                        .param("pageSize", "13")
                )
                .andExpectAll(
                        status().isOk(),
                        header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE),
                        jsonPath("$", notNullValue()),
                        jsonPath("$.content").isArray(),
                        jsonPath("$.pageable").value(Pageable.unpaged().toString()),
                        jsonPath("$.totalPages").value(1),
                        jsonPath("$.totalElements").value(expectedSize),
                        jsonPath("$.first").value(true),
                        jsonPath("$.last").value(true),
                        jsonPath("$.size").value(expectedSize),
                        jsonPath("$.empty").value(false),
                        jsonPath("$.sort", notNullValue()),
                        jsonPath("$.number").value(0),
                        jsonPath("$.numberOfElements").value(expectedSize)
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(mockMvcResult, MvcResponseWithBookContent.class)
                .content();
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
