package mr.cookie.spring6udemy.units.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.controllers.AuthorController;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.services.AuthorService;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import mr.cookie.spring6udemy.services.utils.MvcResponseWithAuthorContent;
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
@WebMvcTest(AuthorController.class)
class AuthorControllerMockMvcTests {

    private static final UUID AUTHOR_ID = UUID.randomUUID();
    private static final AuthorDto AUTHOR_DTO = AuthorDto.builder()
            .firstName("JRR")
            .lastName("Tolkien")
            .build();

    @NotNull
    @Autowired
    private ObjectMapper objectMapper;

    @NotNull
    @Autowired
    private MockMvc mockMvc;

    @NotNull
    @MockBean
    private AuthorService authorService;

    @Test
    void shouldGetAllAuthors() {
        given(this.authorService.findAll(anyInt(), anyInt()))
                .willReturn(new PageImpl<>(List.of(AUTHOR_DTO)));

        var result = this.getAllAuthors(1);

        assertThat(result)
                .isNotNull()
                .containsOnly(AUTHOR_DTO);

        verify(this.authorService).findAll(0, 42);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldGetAuthorById() {
        given(this.authorService.findById(any(UUID.class))).willReturn(Optional.of(AUTHOR_DTO));

        var result = this.getAuthorById(AUTHOR_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(AUTHOR_DTO);

        verify(this.authorService).findById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldGet404WhenCannotFindAuthorById() {
        given(this.authorService.findById(any(UUID.class)))
                .willThrow(new NotFoundEntityException(AUTHOR_ID, AuthorDto.class));

        this.getAuthorByIdAndExpect404(AUTHOR_ID);

        verify(this.authorService).findById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldCreateAuthor() {
        given(this.authorService.create(any(AuthorDto.class))).willReturn(AUTHOR_DTO);

        var result = this.createAuthor(AUTHOR_DTO);

        assertThat(result)
                .isNotNull()
                .isEqualTo(AUTHOR_DTO);

        verify(this.authorService).create(AUTHOR_DTO);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldUpdateAuthor() {
        given(this.authorService.update(any(UUID.class), any(AuthorDto.class))).willReturn(AUTHOR_DTO);

        var result = this.updateAuthor(AUTHOR_ID, AUTHOR_DTO);

        assertThat(result)
                .isNotNull()
                .isEqualTo(AUTHOR_DTO);

        verify(this.authorService).update(AUTHOR_ID, AUTHOR_DTO);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldGet404WhenCannotUpdateAuthorById() {
        given(this.authorService.update(any(UUID.class), any(AuthorDto.class)))
                .willThrow(new NotFoundEntityException(AUTHOR_ID, AuthorDto.class));

        this.updateAuthorAndExpect404(AUTHOR_ID, AUTHOR_DTO);

        verify(this.authorService).update(AUTHOR_ID, AUTHOR_DTO);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldDeleteAuthor() {
        given(this.authorService.deleteById(any(UUID.class))).willReturn(true);

        this.deleteAuthorById(AUTHOR_ID);

        verify(this.authorService).deleteById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

    @Test
    void shouldGet404WhenCannotDeleteAuthorById() {
        given(this.authorService.deleteById(any(UUID.class))).willReturn(false);

        this.deleteAuthorAndExpect404(AUTHOR_ID);

        verify(this.authorService).deleteById(AUTHOR_ID);
        verifyNoMoreInteractions(this.authorService);
    }

    @SneakyThrows
    @NotNull
    private List<AuthorDto> getAllAuthors(int expectedSize) {
        var mockMvcResult = this.mockMvc.perform(get("/author")
                        .param("pageNumber", "0")
                        .param("pageSize", "42")
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

        return this.objectMapper.readValue(mockMvcResult, MvcResponseWithAuthorContent.class)
                .content();
    }

    @SneakyThrows
    @NotNull
    private AuthorDto createAuthor(@NotNull AuthorDto author) {
        var strAuthor = this.mockMvc.perform(post("/author")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(author))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.firstName").value(author.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(author.getLastName()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strAuthor, AuthorDto.class);
    }

    @SneakyThrows
    @NotNull
    private AuthorDto getAuthorById(@NotNull UUID authorId) {
        var strAuthor = this.mockMvc.perform(get("/author/{id}", authorId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
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
    private AuthorDto updateAuthor(@NotNull UUID authorId, @NotNull AuthorDto author) {
        var strAuthor = this.mockMvc.perform(put("/author/{id}", authorId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(author))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.firstName").value(author.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(author.getLastName()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strAuthor, AuthorDto.class);
    }

    @SneakyThrows
    private void updateAuthorAndExpect404(@NotNull UUID authorId, @NotNull AuthorDto author) {
        this.mockMvc.perform(put("/author/{id}", authorId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(author))
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
