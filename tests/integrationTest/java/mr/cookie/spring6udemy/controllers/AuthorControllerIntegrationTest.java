package mr.cookie.spring6udemy.controllers;

import static java.util.UUID.randomUUID;
import static mr.cookie.spring6udemy.providers.dtos.AuthorDtoProvider.provideAuthorDto;
import static mr.cookie.spring6udemy.providers.dtos.AuthorDtoProvider.provideAuthorDtoWithNames;
import static mr.cookie.spring6udemy.providers.entities.AuthorEntityProvider.provideAuthorEntity;
import static mr.cookie.spring6udemy.rest.HttpEntityUtils.createRequestWithHeaders;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;

import mr.cookie.spring6udemy.annotations.IntegrationTest;
import mr.cookie.spring6udemy.assertions.ResponseEntityAssertions;
import mr.cookie.spring6udemy.constants.Constant;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import mr.cookie.spring6udemy.model.mappers.AuthorMapper;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@IntegrationTest
class AuthorControllerIntegrationTest {

    private static final String AUTHOR_PATH = "/author";
    private static final String AUTHOR_BY_ID_PATH = "/author/{id}";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AuthorRepository repository;

    @Autowired
    private AuthorMapper mapper;

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void shouldGetAllAuthorsWithSuccess() {
        var authorRange = 10;
        var createdAuthors = IntStream.range(0, authorRange)
                .mapToObj(ignore -> provideAuthorEntity())
                .map(repository::save)
                .map(mapper::map)
                .toList();

        var uri = UriComponentsBuilder.fromPath(AUTHOR_PATH)
                .buildAndExpand()
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.GET, createRequestWithHeaders(), AuthorDto[].class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatusOk()
                .hasContentTypeAsApplicationJson();

        assertThat(result.getBody())
                .isNotNull()
                .hasSize(authorRange)
                .containsExactlyInAnyOrderElementsOf(createdAuthors);
    }

    @Test
    void shouldGetAuthorByIdWithSuccess() {
        var authorEntity = provideAuthorEntity();
        var authorId = repository.save(authorEntity).getId();

        var uri = UriComponentsBuilder.fromPath(AUTHOR_BY_ID_PATH)
                .buildAndExpand(authorId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.GET, createRequestWithHeaders(), AuthorDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatusOk()
                .hasContentTypeAsApplicationJson();

        assertThat(result.getBody())
                .isNotNull()
                .returns(authorId, AuthorDto::getId)
                .returns(authorEntity.getFirstName(), AuthorDto::getFirstName)
                .returns(authorEntity.getLastName(), AuthorDto::getLastName);
    }

    @Test
    void shouldFailToGetAuthorByIdWith404() {
        var uri = UriComponentsBuilder.fromPath(AUTHOR_BY_ID_PATH)
                .buildAndExpand(randomUUID())
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.GET, createRequestWithHeaders(), AuthorDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateAuthorWithSuccess() {
        var authorDto = provideAuthorEntity();

        var uri = UriComponentsBuilder.fromPath(AUTHOR_PATH)
                .buildAndExpand()
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.POST, createRequestWithHeaders(authorDto), AuthorDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.CREATED)
                .hasContentTypeAsApplicationJson();

        assertThat(result.getBody())
                .isNotNull()
                .returns(authorDto.getFirstName(), AuthorDto::getFirstName)
                .returns(authorDto.getLastName(), AuthorDto::getLastName)
                .doesNotReturn(null, AuthorDto::getId);
    }

    @Test
    void shouldFailWhenCreatingTheSameAuthor409() {
        var authorEntity = provideAuthorEntity();
        repository.save(authorEntity);
        var authorDto = provideAuthorDtoWithNames(
                authorEntity.getFirstName(), authorEntity.getLastName());

        var uri = UriComponentsBuilder.fromPath(AUTHOR_PATH)
                .buildAndExpand()
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.POST, createRequestWithHeaders(authorDto), AuthorDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.CONFLICT);
    }

    static Stream<Consumer<AuthorDto>> authorModifiers() {
        return Stream.of(
                author -> author.setFirstName(null),
                author -> author.setFirstName(Constant.BLANK_STRING),
                author -> author.setFirstName(randomAlphabetic(65)),
                author -> author.setLastName(null),
                author -> author.setLastName(Constant.BLANK_STRING),
                author -> author.setLastName(randomAlphabetic(65))
        );
    }

    @ParameterizedTest
    @MethodSource("authorModifiers")
    void shouldFailToCreateAuthorWithStatus400(@NotNull Consumer<AuthorDto> authorModifier) {
        var authorDto = provideAuthorDto();
        authorModifier.accept(authorDto);

        var uri = UriComponentsBuilder.fromPath(AUTHOR_PATH)
                .buildAndExpand()
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.POST, createRequestWithHeaders(authorDto), AuthorDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldUpdateAuthorWithSuccess() {
        var authorEntity = provideAuthorEntity();
        var authorId = repository.save(authorEntity).getId();
        var authorDto = provideAuthorDto();

        var uri = UriComponentsBuilder.fromPath(AUTHOR_BY_ID_PATH)
                .buildAndExpand(authorId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(authorDto), AuthorDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatusOk()
                .hasContentTypeAsApplicationJson();

        assertThat(result.getBody())
                .isNotNull()
                .returns(authorDto.getFirstName(), AuthorDto::getFirstName)
                .returns(authorDto.getLastName(), AuthorDto::getLastName)
                .returns(authorId, AuthorDto::getId);
    }

    @Test
    void shouldFailToUpdateAuthorWith404() {
        var authorDto = provideAuthorDto();

        var uri = UriComponentsBuilder.fromPath(AUTHOR_BY_ID_PATH)
                .buildAndExpand(randomUUID())
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(authorDto), AuthorDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFailToUpdateAuthorWith409() {
        var authorEntity = provideAuthorEntity();
        var authorId = repository.save(authorEntity).getId();
        var authorDto = provideAuthorDtoWithNames(
                authorEntity.getFirstName(), authorEntity.getLastName());

        var uri = UriComponentsBuilder.fromPath(AUTHOR_BY_ID_PATH)
                .buildAndExpand(authorId)
                .toUri();

        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(authorDto), AuthorDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.CONFLICT);
    }

    @ParameterizedTest
    @MethodSource("authorModifiers")
    void shouldFailToUpdateAuthorWith400(@NotNull Consumer<AuthorDto> authorModifier) {
        var authorEntity = provideAuthorEntity();
        var authorId = repository.save(authorEntity).getId();

        var authorDto = provideAuthorDto();
        authorModifier.accept(authorDto);

        var uri = UriComponentsBuilder.fromPath(AUTHOR_BY_ID_PATH)
                .buildAndExpand(authorId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(authorDto), AuthorDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.BAD_REQUEST);

        assertThat(repository.findById(authorId))
                .isPresent()
                .get()
                .returns(authorEntity.getFirstName(), AuthorEntity::getFirstName)
                .returns(authorEntity.getLastName(), AuthorEntity::getLastName)
                .returns(authorId, AuthorEntity::getId);
    }

    @Test
    void shouldDeleteAuthorByIdWithSuccess() {
        var authorEntity = provideAuthorEntity();
        var authorId = repository.save(authorEntity).getId();

        var uri = UriComponentsBuilder.fromPath(AUTHOR_BY_ID_PATH)
                .buildAndExpand(authorId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.DELETE, new HttpEntity<>(null), Void.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.NO_CONTENT)
                .doesNotHaveHeader(HttpHeaders.CONTENT_TYPE);

        assertThat(repository.findById(authorId))
                .isEmpty();
    }

    @Test
    void shouldNotFailWhenDeleteAuthorDoesNotExist() {
        var authorId = randomUUID();
        var uri = UriComponentsBuilder.fromPath(AUTHOR_BY_ID_PATH)
                .buildAndExpand(authorId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.DELETE, new HttpEntity<>(null), Void.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.NO_CONTENT)
                .doesNotHaveHeader(HttpHeaders.CONTENT_TYPE);

        assertThat(repository.findById(authorId))
                .isEmpty();
    }

}
