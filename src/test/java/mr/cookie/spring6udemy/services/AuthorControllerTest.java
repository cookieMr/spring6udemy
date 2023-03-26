package mr.cookie.spring6udemy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.services.constants.Constant;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
class AuthorControllerTest {

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
        var authorDto = AUTHOR_DTO_SUPPLIER.get();
        var createdAuthor = this.createAuthor(authorDto);

        var result = this.getAllAuthors();

        assertThat(result)
                .isNotNull()
                .containsOnly(createdAuthor);
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

    @SneakyThrows
    @NotNull
    private List<AuthorDto> getAllAuthors() {
        var strAuthors = this.mockMvc.perform(get("/author"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var arrayAuthors = this.objectMapper.readValue(strAuthors, AuthorDto[].class);
        return Arrays.asList(arrayAuthors);
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
