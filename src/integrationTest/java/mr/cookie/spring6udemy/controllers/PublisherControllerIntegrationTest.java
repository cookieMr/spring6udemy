package mr.cookie.spring6udemy.controllers;

import static mr.cookie.spring6udemy.utils.rest.HttpEntityUtils.createRequestWithHeaders;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import mr.cookie.spring6udemy.model.dtos.PublisherDto;
import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import mr.cookie.spring6udemy.model.mappers.PublisherMapper;
import mr.cookie.spring6udemy.repositories.PublisherRepository;
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
class PublisherControllerIntegrationTest {

    private static final String PUBLISHER_PATH = "/publisher";
    private static final String PUBLISHER_BY_ID_PATH = "/publisher/{id}";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PublisherRepository repository;

    @Autowired
    private PublisherMapper mapper;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void shouldGetAllPublishersWithSuccess() {
        var publisherRange = 10;
        var createdPublishers = IntStream.range(0, publisherRange).mapToObj(ignore -> PublisherEntity.builder()
                        .name(RandomStringUtils.randomAlphabetic(25))
                        .address(RandomStringUtils.randomAlphabetic(25))
                        .state(RandomStringUtils.randomAlphabetic(25))
                        .city(RandomStringUtils.randomAlphabetic(25))
                        .zipCode(RandomStringUtils.randomAlphabetic(25))
                        .build())
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
        var publisherEntity = PublisherEntity.builder()
                .name(RandomStringUtils.randomAlphabetic(25))
                .address(RandomStringUtils.randomAlphabetic(25))
                .state(RandomStringUtils.randomAlphabetic(25))
                .city(RandomStringUtils.randomAlphabetic(25))
                .zipCode(RandomStringUtils.randomAlphabetic(25))
                .build();

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
                .buildAndExpand(UUID.randomUUID())
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.GET, createRequestWithHeaders(), PublisherDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentTypeAsApplicationJson();
    }

    @Test
    void shouldCreatePublisherWithSuccess() {
        var publisherDto = PublisherDto.builder()
                .name(RandomStringUtils.randomAlphabetic(25))
                .address(RandomStringUtils.randomAlphabetic(25))
                .state(RandomStringUtils.randomAlphabetic(25))
                .city(RandomStringUtils.randomAlphabetic(25))
                .zipCode(RandomStringUtils.randomAlphabetic(25))
                .build();

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
    @Disabled
    void shouldFailCreatePublisherWith409() {
        // todo: 409 entity already exists
    }

    static Stream<Consumer<PublisherDto>> publisherModifiers() {
        return Stream.of(
                publisher -> publisher.setName(null),
                publisher -> publisher.setName(Constant.BLANK_STRING),
                publisher -> publisher.setName(RandomStringUtils.random(129)),
                publisher -> publisher.setAddress(null),
                publisher -> publisher.setAddress(Constant.BLANK_STRING),
                publisher -> publisher.setAddress(RandomStringUtils.random(129)),
                publisher -> publisher.setCity(null),
                publisher -> publisher.setCity(Constant.BLANK_STRING),
                publisher -> publisher.setCity(RandomStringUtils.random(65)),
                publisher -> publisher.setState(null),
                publisher -> publisher.setState(Constant.BLANK_STRING),
                publisher -> publisher.setState(RandomStringUtils.random(65)),
                publisher -> publisher.setZipCode(null),
                publisher -> publisher.setZipCode(Constant.BLANK_STRING),
                publisher -> publisher.setZipCode(RandomStringUtils.random(65))
        );
    }

    @ParameterizedTest
    @MethodSource("publisherModifiers")
    void shouldFailToCreatePublisherWithStatus400(@NotNull Consumer<PublisherDto> publisherModifier) {
        var publisherDto = PublisherDto.builder()
                .name(RandomStringUtils.randomAlphabetic(25))
                .address(RandomStringUtils.randomAlphabetic(25))
                .state(RandomStringUtils.randomAlphabetic(25))
                .city(RandomStringUtils.randomAlphabetic(25))
                .zipCode(RandomStringUtils.randomAlphabetic(25))
                .build();
        publisherModifier.accept(publisherDto);

        var uri = UriComponentsBuilder.fromPath(PUBLISHER_PATH)
                .buildAndExpand()
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.POST, createRequestWithHeaders(publisherDto), PublisherDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentTypeAsApplicationJson();
    }

    @Test
    void shouldUpdatePublisherWithSuccess() {
        var publisherEntity = PublisherEntity.builder()
                .name(RandomStringUtils.randomAlphabetic(25))
                .address(RandomStringUtils.randomAlphabetic(25))
                .state(RandomStringUtils.randomAlphabetic(25))
                .city(RandomStringUtils.randomAlphabetic(25))
                .zipCode(RandomStringUtils.randomAlphabetic(25))
                .build();

        var publisherId = repository.save(publisherEntity).getId();

        var publisherDto = PublisherDto.builder()
                .name(RandomStringUtils.randomAlphabetic(25))
                .address(RandomStringUtils.randomAlphabetic(25))
                .state(RandomStringUtils.randomAlphabetic(25))
                .city(RandomStringUtils.randomAlphabetic(25))
                .zipCode(RandomStringUtils.randomAlphabetic(25))
                .build();
        var uri = UriComponentsBuilder.fromPath(PUBLISHER_BY_ID_PATH)
                .buildAndExpand(publisherId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(publisherDto), PublisherDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.OK)
                .hasContentTypeAsApplicationJson();

        assertThat(result.getBody())
                .isNotNull()
                .returns(publisherDto.getName(), PublisherDto::getName)
                .doesNotReturn(publisherEntity.getName(), PublisherDto::getName)
                .returns(publisherDto.getAddress(), PublisherDto::getAddress)
                .doesNotReturn(publisherEntity.getAddress(), PublisherDto::getAddress)
                .returns(publisherDto.getState(), PublisherDto::getState)
                .doesNotReturn(publisherEntity.getState(), PublisherDto::getState)
                .returns(publisherDto.getCity(), PublisherDto::getCity)
                .doesNotReturn(publisherEntity.getCity(), PublisherDto::getCity)
                .returns(publisherDto.getZipCode(), PublisherDto::getZipCode)
                .doesNotReturn(publisherEntity.getZipCode(), PublisherDto::getZipCode)
                .returns(publisherId, PublisherDto::getId);
    }

    @Test
    void shouldFailToUpdatePublisherWith404() {
        var publisherDto = PublisherDto.builder()
                .name(RandomStringUtils.randomAlphabetic(25))
                .address(RandomStringUtils.randomAlphabetic(25))
                .state(RandomStringUtils.randomAlphabetic(25))
                .city(RandomStringUtils.randomAlphabetic(25))
                .zipCode(RandomStringUtils.randomAlphabetic(25))
                .build();
        var uri = UriComponentsBuilder.fromPath(PUBLISHER_BY_ID_PATH)
                .buildAndExpand(UUID.randomUUID())
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(publisherDto), PublisherDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentTypeAsApplicationJson();
    }

    @ParameterizedTest
    @MethodSource("publisherModifiers")
    void shouldFailToUpdatePublisherWith400(@NotNull Consumer<PublisherDto> publisherModifier) {
        var publisherEntity = PublisherEntity.builder()
                .name(RandomStringUtils.randomAlphabetic(25))
                .address(RandomStringUtils.randomAlphabetic(25))
                .state(RandomStringUtils.randomAlphabetic(25))
                .city(RandomStringUtils.randomAlphabetic(25))
                .zipCode(RandomStringUtils.randomAlphabetic(25))
                .build();

        var publisherId = repository.save(publisherEntity).getId();

        var publisherDto = PublisherDto.builder()
                .name(RandomStringUtils.randomAlphabetic(25))
                .address(RandomStringUtils.randomAlphabetic(25))
                .state(RandomStringUtils.randomAlphabetic(25))
                .city(RandomStringUtils.randomAlphabetic(25))
                .zipCode(RandomStringUtils.randomAlphabetic(25))
                .build();
        publisherModifier.accept(publisherDto);
        var uri = UriComponentsBuilder.fromPath(PUBLISHER_BY_ID_PATH)
                .buildAndExpand(publisherId)
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.PUT, createRequestWithHeaders(publisherDto), PublisherDto.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentTypeAsApplicationJson();

        assertThat(repository.findById(publisherId))
                .isPresent()
                .get()
                .returns(publisherEntity.getName(), PublisherEntity::getName)
                .doesNotReturn(publisherDto.getName(), PublisherEntity::getName)
                .returns(publisherEntity.getAddress(), PublisherEntity::getAddress)
                .doesNotReturn(publisherDto.getAddress(), PublisherEntity::getAddress)
                .returns(publisherEntity.getState(), PublisherEntity::getState)
                .doesNotReturn(publisherDto.getState(), PublisherEntity::getState)
                .returns(publisherEntity.getCity(), PublisherEntity::getCity)
                .doesNotReturn(publisherDto.getCity(), PublisherEntity::getCity)
                .returns(publisherEntity.getZipCode(), PublisherEntity::getZipCode)
                .doesNotReturn(publisherDto.getZipCode(), PublisherEntity::getZipCode)
                .returns(publisherId, PublisherEntity::getId);
    }

    @Test
    void shouldDeletePublisherByIdWithSuccess() {
        var publisherEntity = PublisherEntity.builder()
                .name(RandomStringUtils.randomAlphabetic(25))
                .address(RandomStringUtils.randomAlphabetic(25))
                .state(RandomStringUtils.randomAlphabetic(25))
                .city(RandomStringUtils.randomAlphabetic(25))
                .zipCode(RandomStringUtils.randomAlphabetic(25))
                .build();

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
    void shouldFailToDeletePublisherByIdWith404() {
        var uri = UriComponentsBuilder.fromPath(PUBLISHER_BY_ID_PATH)
                .buildAndExpand(UUID.randomUUID())
                .toUri();
        var result = restTemplate.exchange(
                uri, HttpMethod.DELETE, new HttpEntity<>(null), Void.class);

        ResponseEntityAssertions.assertThat(result)
                .isNotNull()
                .hasStatus(HttpStatus.NOT_FOUND);
    }

}
