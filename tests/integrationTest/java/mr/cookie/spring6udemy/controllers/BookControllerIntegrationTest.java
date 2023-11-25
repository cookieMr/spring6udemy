package mr.cookie.spring6udemy.controllers;

import static java.util.UUID.randomUUID;
import static mr.cookie.spring6udemy.providers.dtos.BookDtoProvider.provideBookDto;
import static mr.cookie.spring6udemy.providers.dtos.BookDtoProvider.provideBookDtoWithIsbn;
import static mr.cookie.spring6udemy.providers.entities.BookEntityProvider.provideBookEntity;
import static mr.cookie.spring6udemy.rest.HttpEntityUtils.createRequestWithHeaders;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import mr.cookie.spring6udemy.annotations.IntegrationTest;
import mr.cookie.spring6udemy.assertions.ResponseEntityAssertions;
import mr.cookie.spring6udemy.constants.Constant;
import mr.cookie.spring6udemy.model.dtos.BookDto;
import mr.cookie.spring6udemy.model.entities.BookEntity;
import mr.cookie.spring6udemy.model.mappers.BookMapper;
import mr.cookie.spring6udemy.repositories.BookRepository;
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

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void shouldGetAllBooksWithSuccess() {
        var bookRange = 10;
        var createdBooks = IntStream.range(0, bookRange)
                .mapToObj(ignore -> provideBookEntity())
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
        var bookEntity = provideBookEntity();
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
                .buildAndExpand(randomUUID())
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
        var bookDto = provideBookDto();

        var uri = UriComponentsBuilder.fromPath(BOOK_PATH)
                .buildAndExpand()
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.POST, createRequestWithHeaders(bookDto), BookDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.CREATED)
                .hasContentTypeAsApplicationJson();

        assertThat(result.getBody())
                .isNotNull()
                .returns(bookDto.getTitle(), BookDto::getTitle)
                .returns(bookDto.getIsbn(), BookDto::getIsbn)
                .doesNotReturn(null, BookDto::getId);
    }

    @Test
    void shouldFailWhenCreatingTheSameBook409() {
        var bookEntity = provideBookEntity();
        repository.save(bookEntity);
        var bookDto = provideBookDtoWithIsbn(bookEntity.getIsbn());

        var uri = UriComponentsBuilder.fromPath(BOOK_PATH)
                .buildAndExpand()
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.POST, createRequestWithHeaders(bookDto), BookDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.CONFLICT);
    }

    static Stream<Consumer<BookDto>> bookModifiers() {
        return Stream.of(
                book -> book.setTitle(null),
                book -> book.setTitle(Constant.BLANK_STRING),
                book -> book.setTitle(random(129)),
                book -> book.setIsbn(null),
                book -> book.setIsbn(Constant.BLANK_STRING),
                book -> book.setIsbn(random(12)),
                book -> book.setIsbn(random(13)),
                book -> book.setIsbn(random(14)),
                book -> book.setIsbn(random(15))
        );
    }

    @ParameterizedTest
    @MethodSource("bookModifiers")
    void shouldFailToCreateBookWithStatus400(@NotNull Consumer<BookDto> bookModifier) {
        var bookDto = provideBookDto();
        bookModifier.accept(bookDto);

        var uri = UriComponentsBuilder.fromPath(BOOK_PATH)
                .buildAndExpand()
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.POST, createRequestWithHeaders(bookDto), BookDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldUpdateBookWithSuccess() {
        var bookEntity = provideBookEntity();
        var bookId = repository.save(bookEntity).getId();
        var bookDto = provideBookDto();

        var uri = UriComponentsBuilder.fromPath(BOOK_BY_ID_PATH)
                .buildAndExpand(bookId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(bookDto), BookDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatusOk()
                .hasContentTypeAsApplicationJson();

        assertThat(result.getBody())
                .isNotNull()
                .returns(bookDto.getTitle(), BookDto::getTitle)
                .returns(bookDto.getIsbn(), BookDto::getIsbn)
                .returns(bookId, BookDto::getId);
    }

    @Test
    void shouldFailToUpdateBookWith404() {
        var bookDto = provideBookDto();

        var uri = UriComponentsBuilder.fromPath(BOOK_BY_ID_PATH)
                .buildAndExpand(randomUUID())
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(bookDto), BookDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentTypeAsApplicationJson();
    }

    @Test
    void shouldFailToUpdateBookWith409() {
        var bookEntity = provideBookEntity();
        var bookId = repository.save(bookEntity).getId();
        var bookDto = provideBookDtoWithIsbn(bookEntity.getIsbn());

        var uri = UriComponentsBuilder.fromPath(BOOK_BY_ID_PATH)
                .buildAndExpand(bookId)
                .toUri();

        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(bookDto), BookDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.CONFLICT);
    }

    @ParameterizedTest
    @MethodSource("bookModifiers")
    void shouldFailToUpdateBookWith400(@NotNull Consumer<BookDto> bookModifier) {
        var bookEntity = provideBookEntity();
        var bookId = repository.save(bookEntity).getId();

        var bookDto = provideBookDto();
        bookModifier.accept(bookDto);

        var uri = UriComponentsBuilder.fromPath(BOOK_BY_ID_PATH)
                .buildAndExpand(bookId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(bookDto), BookDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.BAD_REQUEST);

        assertThat(repository.findById(bookId))
                .isPresent()
                .get()
                .returns(bookEntity.getTitle(), BookEntity::getTitle)
                .returns(bookEntity.getIsbn(), BookEntity::getIsbn)
                .returns(bookId, BookEntity::getId);
    }

    @Test
    void shouldDeleteBookByIdWithSuccess() {
        var bookEntity = provideBookEntity();
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
    void shouldNotFailWhenDeleteBookDoesNotExist() {
        var bookId = randomUUID();
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

}
