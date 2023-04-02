package mr.cookie.spring6udemy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.services.utils.Constant;
import mr.cookie.spring6udemy.services.utils.MvcResponseWithAuthorContent;
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
        properties = "app.pagination.default-page-size=" + AuthorControllerTest.TEST_PAGE_SIZE
)
@AutoConfigureMockMvc
class AuthorControllerTest {

    public static final int TEST_PAGE_SIZE = 13;

    private static final Supplier<AuthorDto> AUTHOR_DTO_SUPPLIER = () -> AuthorDto.builder()
            .firstName("JRR")
            .lastName("Tolkien")
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
    void shouldGetAllAuthors() {
        var createdAuthors = IntStream.range(0, TEST_PAGE_SIZE).mapToObj($ -> AuthorDto.builder()
                        .firstName(RandomStringUtils.randomAlphabetic(25))
                        .lastName(RandomStringUtils.randomAlphabetic(25))
                        .build())
                .map(this::createAuthor)
                .toList();

        var result = this.getAllAuthors(TEST_PAGE_SIZE, true, 1);

        assertThat(result)
                .isNotNull()
                .containsAll(createdAuthors);
    }

    @Test
    @Rollback
    @Transactional
    void shouldGetFirstPageOfAuthors() {
        var createdAuthors = IntStream.range(0, 2 * TEST_PAGE_SIZE).mapToObj($ -> AuthorDto.builder()
                        .firstName(RandomStringUtils.randomAlphabetic(25))
                        .lastName(RandomStringUtils.randomAlphabetic(25))
                        .build())
                .map(this::createAuthor)
                .toList();

        var result = this.getAllAuthors(createdAuthors.size(), false, 2);

        assertThat(result)
                .isNotNull()
                .containsAll(createdAuthors.subList(0, TEST_PAGE_SIZE));
    }

    @Test
    @Rollback
    @Transactional
    void shouldGetSecondPageOfAuthors() {
        var createdAuthors = IntStream.range(0, 3 * TEST_PAGE_SIZE).mapToObj($ -> AuthorDto.builder()
                        .firstName(RandomStringUtils.randomAlphabetic(25))
                        .lastName(RandomStringUtils.randomAlphabetic(25))
                        .build())
                .map(this::createAuthor)
                .toList();

        var result = this.getSecondPageOfAuthors(createdAuthors.size(), false, 3);

        assertThat(result)
                .isNotNull()
                .containsAll(createdAuthors.subList(TEST_PAGE_SIZE, TEST_PAGE_SIZE));
    }

    @Test
    @Rollback
    @Transactional
    void shouldCreateAndThenGetAuthorById() {
        var authorDto = AUTHOR_DTO_SUPPLIER.get();

        var authorId = this.createAuthor(authorDto).getId();
        var result = this.getAuthorById(authorId);

        assertThat(result)
                .isNotNull()
                .matches(dto -> authorId.equals(dto.getId()))
                .returns(authorId, AuthorDto::getId)
                .returns(authorDto.getFirstName(), AuthorDto::getFirstName)
                .returns(authorDto.getLastName(), AuthorDto::getLastName);
    }

    static Stream<Consumer<AuthorDto>> authorModifiers() {
        return Stream.of(
                author -> author.setFirstName(null),
                author -> author.setFirstName(Constant.BLANK_STRING),
                author -> author.setFirstName(RandomStringUtils.random(65)),
                author -> author.setLastName(null),
                author -> author.setLastName(Constant.BLANK_STRING),
                author -> author.setLastName(RandomStringUtils.random(65))
        );
    }

    @ParameterizedTest
    @MethodSource("authorModifiers")
    void shouldFailToCreateAuthor(@NotNull Consumer<AuthorDto> authorModifier) {
        var authorDto = AUTHOR_DTO_SUPPLIER.get();
        authorModifier.accept(authorDto);

        this.createAuthorAndExpect400(authorDto);
    }

    @Test
    void shouldReturn404WhenAuthorIsNotFound() {
        var authorId = UUID.randomUUID();
        this.getAuthorByIdAndExpect404(authorId);
    }

    @Test
    @Rollback
    @Transactional
    void shouldCreateAuthor() {
        var authorDto = AUTHOR_DTO_SUPPLIER.get();

        var result = this.createAuthor(authorDto);

        assertThat(result).isNotNull();
        assertAll(
                () -> assertThat(result)
                        .returns(authorDto.getFirstName(), AuthorDto::getFirstName)
                        .returns(authorDto.getLastName(), AuthorDto::getLastName),
                () -> assertThat(result.getId())
                        .isNotNull()
        );
    }

    @Test
    @Rollback
    @Transactional
    void shouldUpdateAuthor() {
        var authorDto = AUTHOR_DTO_SUPPLIER.get();
        var createdAuthor = this.createAuthor(authorDto);

        var result = this.updateAuthor(createdAuthor);

        assertThat(result)
                .isNotNull()
                .matches(dto -> dto.getId().equals(createdAuthor.getId()))
                .returns(authorDto.getFirstName(), AuthorDto::getFirstName)
                .returns(authorDto.getLastName(), AuthorDto::getLastName);
    }

    @ParameterizedTest
    @MethodSource("authorModifiers")
    void shouldFailToUpdateAuthor(@NotNull Consumer<AuthorDto> authorModifier) {
        var authorDto = AUTHOR_DTO_SUPPLIER.get();
        var createdAuthor = this.createAuthor(authorDto);
        authorModifier.accept(createdAuthor);

        this.updateAuthorAndExpect400(createdAuthor);
    }

    @Test
    void shouldReturn404WhenUpdatingAuthorIsNotFound() {
        var authorDto = AUTHOR_DTO_SUPPLIER.get();
        var authorId = UUID.randomUUID();
        this.updateAuthorAndExpect404(authorId, authorDto);
    }

    @Test
    @Rollback
    @Transactional
    void shouldDeleteExistingAuthor() {
        var authorDto = AUTHOR_DTO_SUPPLIER.get();

        var authorId = this.createAuthor(authorDto).getId();
        this.deleteAuthorById(authorId);
    }

    @Test
    void shouldReturn404WhenDeletingAuthorIsNotFound() {
        var authorId = UUID.randomUUID();
        this.deleteAuthorAndExpect404(authorId);
    }

    @NotNull
    private List<AuthorDto> getAllAuthors(int expectedSize, boolean last, int totalPages) {
        return validateResponseAndGetAuthors(
                get("/author"), expectedSize, last, totalPages, 0, true, 0
        );
    }

    @NotNull
    private List<AuthorDto> getSecondPageOfAuthors(int expectedSize, boolean last, int totalPages) {
        return validateResponseAndGetAuthors(
                get("/author").param("pageNumber", "1"), expectedSize, last, totalPages, TEST_PAGE_SIZE, false, 1
        );
    }

    @SneakyThrows
    @NotNull
    private List<AuthorDto> validateResponseAndGetAuthors(
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

        return this.objectMapper.readValue(mockMvcResult, MvcResponseWithAuthorContent.class)
                .content();
    }

    @SneakyThrows
    @NotNull
    private AuthorDto createAuthor(@NotNull AuthorDto authorDto) {
        var strAuthor = this.mockMvc.perform(post("/author")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(authorDto))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value(authorDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(authorDto.getLastName()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strAuthor, AuthorDto.class);
    }

    @SneakyThrows
    private void createAuthorAndExpect400(@NotNull AuthorDto authorDto) {
        this.mockMvc.perform(post("/author")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(authorDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @NotNull
    private AuthorDto getAuthorById(@NotNull UUID authorId) {
        var strAuthor = this.mockMvc.perform(get("/author/{id}", authorId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(authorId.toString()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strAuthor, AuthorDto.class);
    }

    @SneakyThrows
    private void getAuthorByIdAndExpect404(@NotNull UUID authorId) {
        this.mockMvc.perform(get("/author/{id}", authorId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @NotNull
    private AuthorDto updateAuthor(@NotNull AuthorDto authorDto) {
        var strAuthor = this.mockMvc.perform(put("/author/{id}", authorDto.getId())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(authorDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(authorDto.getId().toString()))
                .andExpect(jsonPath("$.firstName").value(authorDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(authorDto.getLastName()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strAuthor, AuthorDto.class);
    }

    @SneakyThrows
    private void updateAuthorAndExpect400(@NotNull AuthorDto authorDto) {
        this.mockMvc.perform(put("/author/{id}", authorDto.getId())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(authorDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    private void updateAuthorAndExpect404(@NotNull UUID authorId, @NotNull AuthorDto authorDto) {
        this.mockMvc.perform(put("/author/{id}", authorId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(authorDto))
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    private void deleteAuthorById(@NotNull UUID authorId) {
        this.mockMvc.perform(delete("/author/{id}", authorId))
                .andExpect(status().isNoContent());
    }

    @SneakyThrows
    private void deleteAuthorAndExpect404(@NotNull UUID authorId) {
        this.mockMvc.perform(delete("/author/{id}", authorId))
                .andExpect(status().isNotFound());
    }

}
