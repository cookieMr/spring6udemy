package mr.cookie.spring6udemy.controllers;

import static java.util.UUID.randomUUID;
import static mr.cookie.spring6udemy.providers.dtos.PublisherDtoProvider.providePublisherDto;
import static mr.cookie.spring6udemy.providers.dtos.PublisherDtoProvider.providePublisherDtoWithName;
import static mr.cookie.spring6udemy.providers.entities.PublisherEntityProvider.providePublisherEntity;
import static mr.cookie.spring6udemy.rest.HttpEntityUtils.createRequestWithHeaders;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.assertj.core.api.Assertions.assertThat;

import mr.cookie.spring6udemy.annotations.IntegrationTest;
import mr.cookie.spring6udemy.assertions.ResponseEntityAssertions;
import mr.cookie.spring6udemy.constants.Constant;
import mr.cookie.spring6udemy.exceptions.ErrorDto;
import mr.cookie.spring6udemy.model.dtos.PublisherDto;
import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import mr.cookie.spring6udemy.model.mappers.PublisherMapper;
import mr.cookie.spring6udemy.repositories.PublisherRepository;
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
class PublisherControllerIntegrationTest {

    private static final String PUBLISHER_PATH = "/publisher";
    private static final String PUBLISHER_BY_ID_PATH = "/publisher/{id}";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PublisherRepository repository;

    @Autowired
    private PublisherMapper mapper;

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void shouldGetAllPublishersWithSuccess() {
        var publisherRange = 10;
        var createdPublishers = IntStream.range(0, publisherRange).mapToObj(ignore -> providePublisherEntity())
                .map(repository::save)
                .map(mapper::map)
                .toList();

        var uri = UriComponentsBuilder.fromPath(PUBLISHER_PATH)
                .buildAndExpand()
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.GET, createRequestWithHeaders(), PublisherDto[].class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatusOk()
                .hasContentTypeAsApplicationJson();

        assertThat(result.getBody())
                .isNotNull()
                .hasSize(publisherRange)
                .containsExactlyInAnyOrderElementsOf(createdPublishers);
    }

    @Test
    void shouldGetPublisherByIdWithSuccess() {
        var publisherEntity = providePublisherEntity();
        var publisherId = repository.save(publisherEntity).getId();

        var uri = UriComponentsBuilder.fromPath(PUBLISHER_BY_ID_PATH)
                .buildAndExpand(publisherId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.GET, createRequestWithHeaders(), PublisherDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatusOk()
                .hasContentTypeAsApplicationJson();

        assertThat(result.getBody())
                .isNotNull()
                .returns(publisherId, PublisherDto::getId)
                .returns(publisherEntity.getName(), PublisherDto::getName)
                .returns(publisherEntity.getAddress(), PublisherDto::getAddress)
                .returns(publisherEntity.getState(), PublisherDto::getState)
                .returns(publisherEntity.getCity(), PublisherDto::getCity)
                .returns(publisherEntity.getZipCode(), PublisherDto::getZipCode);
    }

    @Test
    void shouldFailToGetPublisherByIdWith404() {
        var uri = UriComponentsBuilder.fromPath(PUBLISHER_BY_ID_PATH)
                .buildAndExpand(randomUUID())
                .toUri();
        var result = restTemplate.getForEntity(uri, ErrorDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreatePublisherWithSuccess() {
        var publisherDto = providePublisherDto();

        var uri = UriComponentsBuilder.fromPath(PUBLISHER_PATH)
                .buildAndExpand()
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.POST, createRequestWithHeaders(publisherDto), PublisherDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.CREATED)
                .hasContentTypeAsApplicationJson();

        assertThat(result.getBody())
                .isNotNull()
                .returns(publisherDto.getName(), PublisherDto::getName)
                .returns(publisherDto.getAddress(), PublisherDto::getAddress)
                .returns(publisherDto.getState(), PublisherDto::getState)
                .returns(publisherDto.getCity(), PublisherDto::getCity)
                .returns(publisherDto.getZipCode(), PublisherDto::getZipCode)
                .doesNotReturn(null, PublisherDto::getId);
    }

    @Test
    void shouldFailWhenCreatingTheSamePublisher409() {
        var publisherEntity = providePublisherEntity();
        repository.save(publisherEntity);
        var publisherDto = providePublisherDtoWithName(publisherEntity.getName());

        var uri = UriComponentsBuilder.fromPath(PUBLISHER_PATH)
                .buildAndExpand()
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.POST, createRequestWithHeaders(publisherDto), PublisherDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.CONFLICT);
    }

    static Stream<Consumer<PublisherDto>> publisherModifiers() {
        return Stream.of(
                publisher -> publisher.setName(null),
                publisher -> publisher.setName(Constant.BLANK_STRING),
                publisher -> publisher.setName(random(129)),
                publisher -> publisher.setAddress(null),
                publisher -> publisher.setAddress(Constant.BLANK_STRING),
                publisher -> publisher.setAddress(random(129)),
                publisher -> publisher.setCity(null),
                publisher -> publisher.setCity(Constant.BLANK_STRING),
                publisher -> publisher.setCity(random(65)),
                publisher -> publisher.setState(null),
                publisher -> publisher.setState(Constant.BLANK_STRING),
                publisher -> publisher.setState(random(65)),
                publisher -> publisher.setZipCode(null),
                publisher -> publisher.setZipCode(Constant.BLANK_STRING),
                publisher -> publisher.setZipCode(random(65))
        );
    }

    @ParameterizedTest
    @MethodSource("publisherModifiers")
    void shouldFailToCreatePublisherWithStatus400(@NotNull Consumer<PublisherDto> publisherModifier) {
        var publisherDto = providePublisherDto();
        publisherModifier.accept(publisherDto);

        var uri = UriComponentsBuilder.fromPath(PUBLISHER_PATH)
                .buildAndExpand()
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.POST, createRequestWithHeaders(publisherDto), PublisherDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldUpdatePublisherWithSuccess() {
        var publisherEntity = providePublisherEntity();
        var publisherDto = providePublisherDto();
        var publisherId = repository.save(publisherEntity).getId();

        var uri = UriComponentsBuilder.fromPath(PUBLISHER_BY_ID_PATH)
                .buildAndExpand(publisherId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(publisherDto), PublisherDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatusOk()
                .hasContentTypeAsApplicationJson();

        assertThat(result.getBody())
                .isNotNull()
                .returns(publisherDto.getName(), PublisherDto::getName)
                .returns(publisherDto.getAddress(), PublisherDto::getAddress)
                .returns(publisherDto.getState(), PublisherDto::getState)
                .returns(publisherDto.getCity(), PublisherDto::getCity)
                .returns(publisherDto.getZipCode(), PublisherDto::getZipCode)
                .returns(publisherId, PublisherDto::getId);
    }

    @Test
    void shouldFailToUpdatePublisherWith404() {
        var publisherDto = providePublisherDto();

        var uri = UriComponentsBuilder.fromPath(PUBLISHER_BY_ID_PATH)
                .buildAndExpand(randomUUID())
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(publisherDto), PublisherDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFailToUpdatePublisherWith409() {
        var publisherEntity = providePublisherEntity();
        var publisherId = repository.save(publisherEntity).getId();
        var publisherDto = providePublisherDtoWithName(publisherEntity.getName());

        var uri = UriComponentsBuilder.fromPath(PUBLISHER_BY_ID_PATH)
                .buildAndExpand(publisherId)
                .toUri();

        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(publisherDto), PublisherDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.CONFLICT);
    }

    @ParameterizedTest
    @MethodSource("publisherModifiers")
    void shouldFailToUpdatePublisherWith400(@NotNull Consumer<PublisherDto> publisherModifier) {
        var publisherEntity = providePublisherEntity();
        var publisherId = repository.save(publisherEntity).getId();

        var publisherDto = providePublisherDto();
        publisherModifier.accept(publisherDto);

        var uri = UriComponentsBuilder.fromPath(PUBLISHER_BY_ID_PATH)
                .buildAndExpand(publisherId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(publisherDto), PublisherDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.BAD_REQUEST);

        assertThat(repository.findById(publisherId))
                .isPresent()
                .get()
                .returns(publisherEntity.getName(), PublisherEntity::getName)
                .returns(publisherEntity.getAddress(), PublisherEntity::getAddress)
                .returns(publisherEntity.getState(), PublisherEntity::getState)
                .returns(publisherEntity.getCity(), PublisherEntity::getCity)
                .returns(publisherEntity.getZipCode(), PublisherEntity::getZipCode)
                .returns(publisherId, PublisherEntity::getId);
    }

    @Test
    void shouldDeletePublisherByIdWithSuccess() {
        var publisherEntity = providePublisherEntity();
        var publisherId = repository.save(publisherEntity).getId();

        var uri = UriComponentsBuilder.fromPath(PUBLISHER_BY_ID_PATH)
                .buildAndExpand(publisherId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.DELETE, new HttpEntity<>(null), Void.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.NO_CONTENT)
                .doesNotHaveHeader(HttpHeaders.CONTENT_TYPE);

        assertThat(repository.findById(publisherId))
                .isEmpty();
    }

    @Test
    void shouldNotFailWhenDeletePublisherDoesNotExist() {
        var publisherId = randomUUID();
        var uri = UriComponentsBuilder.fromPath(PUBLISHER_BY_ID_PATH)
                .buildAndExpand(publisherId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.DELETE, new HttpEntity<>(null), Void.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.NO_CONTENT)
                .doesNotHaveHeader(HttpHeaders.CONTENT_TYPE);

        assertThat(repository.findById(publisherId))
                .isEmpty();
    }

}
