package mr.cookie.spring6udemy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.services.utils.Constant;
import mr.cookie.spring6udemy.services.utils.MvcResponseWithBookContent;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
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
@SpringBootTest(
        properties = "app.pagination.default-page-size=" + BookControllerTests.TEST_PAGE_SIZE
)
@AutoConfigureMockMvc
class BookControllerTests {

    public static final int TEST_PAGE_SIZE = 7;

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
    @Rollback
    @Transactional
    void shouldGetAllBooks() {
        var createdBooks = IntStream.range(0, TEST_PAGE_SIZE).mapToObj($ -> BookDto.builder()
                        .title(RandomStringUtils.randomAlphabetic(25))
                        .isbn("%s-%s".formatted(
                                RandomStringUtils.randomNumeric(3),
                                RandomStringUtils.randomNumeric(10)
                        ))
                        .build())
                .map(this::createBook)
                .toList();

        var result = this.getAllBooks(TEST_PAGE_SIZE, true, 1);

        assertThat(result)
                .isNotNull()
                .containsAll(createdBooks);
    }

    @Test
    @Rollback
    @Transactional
    void shouldGetFirstPageOfBooks() {
        var createdBooks = IntStream.range(0, 2 * TEST_PAGE_SIZE).mapToObj($ -> BookDto.builder()
                        .title(RandomStringUtils.randomAlphabetic(25))
                        .isbn("%s-%s".formatted(
                                RandomStringUtils.randomNumeric(3),
                                RandomStringUtils.randomNumeric(10)
                        ))
                        .build())
                .map(this::createBook)
                .toList();

        var result = this.getAllBooks(createdBooks.size(), false, 2);

        assertThat(result)
                .isNotNull()
                .containsAll(createdBooks.subList(0, TEST_PAGE_SIZE));
    }

    @Test
    @Rollback
    @Transactional
    void shouldGetSecondPageOfBooks() {
        var createdBooks = IntStream.range(0, 3 * TEST_PAGE_SIZE).mapToObj($ -> BookDto.builder()
                        .title(RandomStringUtils.randomAlphabetic(25))
                        .isbn("%s-%s".formatted(
                                RandomStringUtils.randomNumeric(3),
                                RandomStringUtils.randomNumeric(10)
                        ))
                        .build())
                .map(this::createBook)
                .toList();

        var result = this.getSecondPageOfBooks(createdBooks.size(), false, 3);

        assertThat(result)
                .isNotNull()
                .containsAll(createdBooks.subList(TEST_PAGE_SIZE, TEST_PAGE_SIZE));
    }

    @Test
    @Rollback
    @Transactional
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
                book -> book.setTitle(Constant.BLANK_STRING),
                book -> book.setTitle(RandomStringUtils.random(129)),
                book -> book.setIsbn(null),
                book -> book.setIsbn(Constant.BLANK_STRING),
                book -> book.setIsbn(RandomStringUtils.random(12)),
                book -> book.setIsbn(RandomStringUtils.random(13)),
                book -> book.setIsbn(RandomStringUtils.random(14)),
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
    @Rollback
    @Transactional
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
    @Rollback
    @Transactional
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

    @Rollback
    @Transactional
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
    @Rollback
    @Transactional
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

    @NotNull
    private List<BookDto> getAllBooks(int expectedSize, boolean last, int totalPages) {
        return validateResponseAndGetBooks(
                get("/book"), expectedSize, last, totalPages, 0, true, 0
        );
    }

    @NotNull
    private List<BookDto> getSecondPageOfBooks(int expectedSize, boolean last, int totalPages) {
        return validateResponseAndGetBooks(
                get("/book").param("pageNumber", "1"), expectedSize, last, totalPages, TEST_PAGE_SIZE, false, 1
        );
    }

    @SneakyThrows
    @NotNull
    private List<BookDto> validateResponseAndGetBooks(
            @NotNull MockHttpServletRequestBuilder builder,
            int expectedSize,
            boolean last,
            int totalPages,
            int offset,
            boolean first,
            int number
    ) {
        var mockMvcResult = this.mockMvc.perform(builder)
                .andExpectAll(
                        status().isOk(),
                        header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE),
                        jsonPath("$", notNullValue()),
                        jsonPath("$.content").isArray(),
                        jsonPath("$.pageable", notNullValue()),
                        jsonPath("$.pageable.sort.empty").value(false),
                        jsonPath("$.pageable.sort.sorted").value(true),
                        jsonPath("$.pageable.sort.unsorted").value(false),
                        jsonPath("$.pageable.offset").value(offset),
                        jsonPath("$.pageable.pageNumber").value(number),
                        jsonPath("$.pageable.pageSize").value(TEST_PAGE_SIZE),
                        jsonPath("$.pageable.paged").value(true),
                        jsonPath("$.pageable.unpaged").value(false),
                        jsonPath("$.totalPages").value(totalPages),
                        jsonPath("$.totalElements").value(expectedSize),
                        jsonPath("$.first").value(first),
                        jsonPath("$.last").value(last),
                        jsonPath("$.size").value(TEST_PAGE_SIZE),
                        jsonPath("$.empty").value(false),
                        jsonPath("$.sort", notNullValue()),
                        jsonPath("$.sort.empty").value(false),
                        jsonPath("$.sort.sorted").value(true),
                        jsonPath("$.sort.unsorted").value(false),
                        jsonPath("$.number").value(number),
                        jsonPath("$.numberOfElements").value(TEST_PAGE_SIZE)
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(mockMvcResult, MvcResponseWithBookContent.class)
                .content();
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
