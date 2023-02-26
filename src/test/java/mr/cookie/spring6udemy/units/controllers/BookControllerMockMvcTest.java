package mr.cookie.spring6udemy.units.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.controllers.BookController;
import mr.cookie.spring6udemy.model.model.Book;
import mr.cookie.spring6udemy.services.BookService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerMockMvcTest {

    private static final long BOOK_ID = 1L;
    private static final Book BOOK = Book.builder()
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
        given(this.bookService.findAll()).willReturn(Collections.singletonList(BOOK));

        var result = this.getAllBooks();

        assertThat(result)
                .isNotNull()
                .containsOnly(BOOK);

        verify(this.bookService).findAll();
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldGetBookById() {
        given(this.bookService.findById(anyLong())).willReturn(BOOK);

        var result = this.getBookById(BOOK_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(BOOK);

        verify(this.bookService).findById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldCreateBook() {
        given(this.bookService.create(any(Book.class))).willReturn(BOOK);

        var result = this.createBook(BOOK);

        assertThat(result)
                .isNotNull()
                .isEqualTo(BOOK);

        verify(this.bookService).create(BOOK);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldUpdateBook() {
        given(this.bookService.update(anyLong(), any(Book.class))).willReturn(BOOK);

        var result = this.updateBook(BOOK_ID, BOOK);

        assertThat(result)
                .isNotNull()
                .isEqualTo(BOOK);

        verify(this.bookService).update(BOOK_ID, BOOK);
        verifyNoMoreInteractions(this.bookService);
    }

    @Test
    void shouldDeleteBook() {
        this.deleteBookById(BOOK_ID);

        verify(this.bookService).deleteById(BOOK_ID);
        verifyNoMoreInteractions(this.bookService);
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
