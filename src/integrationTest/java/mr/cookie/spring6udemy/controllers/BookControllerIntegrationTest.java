package mr.cookie.spring6udemy.controllers;

import static mr.cookie.spring6udemy.utils.rest.HttpEntityUtils.createRequestWithHeaders;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.model.entities.BookEntity;
import mr.cookie.spring6udemy.model.mappers.BookMapper;
import mr.cookie.spring6udemy.repositories.BookRepository;
import mr.cookie.spring6udemy.utils.annotations.IntegrationTest;
import mr.cookie.spring6udemy.utils.assertions.ResponseEntityAssertions;
import mr.cookie.spring6udemy.utils.constants.Constant;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

@IntegrationTest
class BookControllerIntegrationTest {

    private static final String BOOK_PATH = "/book";
    private static final String BOOK_BY_ID_PATH = "/book/{id}";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookRepository repository;

    @Autowired
    private BookMapper mapper;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void shouldGetAllBooksWithSuccess() {
        var bookRange = 10;
        var createdBooks = IntStream.range(0, bookRange).mapToObj(ignore -> BookEntity.builder()
                        .title(RandomStringUtils.randomAlphabetic(25))
                        .isbn("%s-%s".formatted(
                                RandomStringUtils.randomNumeric(3),
                                RandomStringUtils.randomNumeric(10)))
                        .build())
                .map(repository::save)
                .map(mapper::map)
                .toList();

        var uri = UriComponentsBuilder.fromPath(BOOK_PATH)
                .buildAndExpand()
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.GET, createRequestWithHeaders(), BookDto[].class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatusOk()
                .hasContentTypeAsApplicationJson();

        assertThat(result.getBody())
                .isNotNull()
                .hasSize(bookRange)
                .containsExactlyInAnyOrderElementsOf(createdBooks);
    }

    @Test
    void shouldGetBookByIdWithSuccess() {
        var bookEntity = BookEntity.builder()
                .title(RandomStringUtils.randomAlphabetic(25))
                .isbn("%s-%s".formatted(
                        RandomStringUtils.randomNumeric(3),
                        RandomStringUtils.randomNumeric(10)))
                .build();

        var bookId = repository.save(bookEntity).getId();

        var uri = UriComponentsBuilder.fromPath(BOOK_BY_ID_PATH)
                .buildAndExpand(bookId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.GET, createRequestWithHeaders(), BookDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatusOk()
                .hasContentTypeAsApplicationJson();

        assertThat(result.getBody())
                .isNotNull()
                .returns(bookId, BookDto::getId)
                .returns(bookEntity.getTitle(), BookDto::getTitle)
                .returns(bookEntity.getIsbn(), BookDto::getIsbn);
    }

    @Test
    void shouldFailToGetBookByIdWith404() {
        var uri = UriComponentsBuilder.fromPath(BOOK_BY_ID_PATH)
                .buildAndExpand(UUID.randomUUID())
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.GET, createRequestWithHeaders(), BookDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentTypeAsApplicationJson();
    }

    @Test
    void shouldCreateBookWithSuccess() {
        var bookEntity = BookEntity.builder()
                .title(RandomStringUtils.randomAlphabetic(25))
                .isbn("%s-%s".formatted(
                        RandomStringUtils.randomNumeric(3),
                        RandomStringUtils.randomNumeric(10)))
                .build();

        var uri = UriComponentsBuilder.fromPath(BOOK_PATH)
                .buildAndExpand()
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.POST, createRequestWithHeaders(bookEntity), BookDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.CREATED)
                .hasContentTypeAsApplicationJson();

        assertThat(result.getBody())
                .isNotNull()
                .returns(bookEntity.getTitle(), BookDto::getTitle)
                .returns(bookEntity.getIsbn(), BookDto::getIsbn)
                .doesNotReturn(null, BookDto::getId);
    }

    @Test
    @Disabled
    void shouldFailCreateBookWith409() {
        // todo: 409 entity already exists
    }

    static Stream<Consumer<BookDto>> bookMalformModifiers() {
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
    @MethodSource("bookMalformModifiers")
    void shouldFailToCreateBookWithStatus400(@NotNull Consumer<BookDto> bookModifier) {
        var bookDto = BookDto.builder()
                .title(RandomStringUtils.randomAlphabetic(25))
                .isbn("%s-%s".formatted(
                        RandomStringUtils.randomNumeric(3),
                        RandomStringUtils.randomNumeric(10)))
                .build();
        bookModifier.accept(bookDto);

        var uri = UriComponentsBuilder.fromPath(BOOK_PATH)
                .buildAndExpand()
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.POST, createRequestWithHeaders(bookDto), BookDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentTypeAsApplicationJson();
    }

    @Test
    void shouldUpdateBookWithSuccess() {
        var bookEntity = BookEntity.builder()
                .title(RandomStringUtils.randomAlphabetic(25))
                .isbn("%s-%s".formatted(
                        RandomStringUtils.randomNumeric(3),
                        RandomStringUtils.randomNumeric(10)))
                .build();

        var bookId = repository.save(bookEntity).getId();

        var bookDto = BookDto.builder()
                .title(RandomStringUtils.randomAlphabetic(25))
                .isbn("%s-%s".formatted(
                        RandomStringUtils.randomNumeric(3),
                        RandomStringUtils.randomNumeric(10)))
                .build();
        var uri = UriComponentsBuilder.fromPath(BOOK_BY_ID_PATH)
                .buildAndExpand(bookId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(bookDto), BookDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.OK)
                .hasContentTypeAsApplicationJson();

        assertThat(result.getBody())
                .isNotNull()
                .returns(bookDto.getTitle(), BookDto::getTitle)
                .doesNotReturn(bookEntity.getTitle(), BookDto::getTitle)
                .returns(bookDto.getIsbn(), BookDto::getIsbn)
                .doesNotReturn(bookEntity.getIsbn(), BookDto::getIsbn)
                .returns(bookId, BookDto::getId);
    }

    @Test
    void shouldFailToUpdateBookWith404() {
        var bookDto = BookDto.builder()
                .title(RandomStringUtils.randomAlphabetic(25))
                .isbn("%s-%s".formatted(
                        RandomStringUtils.randomNumeric(3),
                        RandomStringUtils.randomNumeric(10)))
                .build();
        var uri = UriComponentsBuilder.fromPath(BOOK_BY_ID_PATH)
                .buildAndExpand(UUID.randomUUID())
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(bookDto), BookDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentTypeAsApplicationJson();
    }

    @ParameterizedTest
    @MethodSource("bookMalformModifiers")
    void shouldFailToUpdateAuthorWith400(@NotNull Consumer<BookDto> bookModifier) {
        var bookEntity = BookEntity.builder()
                .title(RandomStringUtils.randomAlphabetic(25))
                .isbn("%s-%s".formatted(
                        RandomStringUtils.randomNumeric(3),
                        RandomStringUtils.randomNumeric(10)))
                .build();

        var bookId = repository.save(bookEntity).getId();

        var bookDto = BookDto.builder()
                .title(RandomStringUtils.randomAlphabetic(25))
                .isbn("%s-%s".formatted(
                        RandomStringUtils.randomNumeric(3),
                        RandomStringUtils.randomNumeric(10)))
                .build();
        bookModifier.accept(bookDto);
        var uri = UriComponentsBuilder.fromPath(BOOK_BY_ID_PATH)
                .buildAndExpand(bookId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(bookDto), BookDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentTypeAsApplicationJson();

        assertThat(repository.findById(bookId))
                .isPresent()
                .get()
                .returns(bookEntity.getTitle(), BookEntity::getTitle)
                .doesNotReturn(bookDto.getTitle(), BookEntity::getTitle)
                .returns(bookEntity.getIsbn(), BookEntity::getIsbn)
                .doesNotReturn(bookDto.getIsbn(), BookEntity::getIsbn)
                .returns(bookId, BookEntity::getId);
    }

    @Test
    void shouldDeleteBookByIdWithSuccess() {
        var bookEntity = BookEntity.builder()
                .title(RandomStringUtils.randomAlphabetic(25))
                .isbn("%s-%s".formatted(
                        RandomStringUtils.randomNumeric(3),
                        RandomStringUtils.randomNumeric(10)))
                .build();

        var bookId = repository.save(bookEntity).getId();
        var uri = UriComponentsBuilder.fromPath(BOOK_BY_ID_PATH)
                .buildAndExpand(bookId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.DELETE, new HttpEntity<>(null), Void.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.NO_CONTENT)
                .doesNotHaveHeader(HttpHeaders.CONTENT_TYPE);

        assertThat(repository.findById(bookId))
                .isEmpty();
    }

    @Test
    void shouldFailToDeleteBookByIdWith404() {
        var uri = UriComponentsBuilder.fromPath(BOOK_BY_ID_PATH)
                .buildAndExpand(UUID.randomUUID())
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.DELETE, new HttpEntity<>(null), Void.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.NOT_FOUND);
    }

}
