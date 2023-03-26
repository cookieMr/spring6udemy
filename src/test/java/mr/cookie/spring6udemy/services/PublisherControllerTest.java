package mr.cookie.spring6udemy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.model.dtos.PublisherDto;
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
class PublisherControllerTest {

    private static final Supplier<PublisherDto> PUBLISHER_DTO_SUPPLIER = () -> PublisherDto.builder()
            .name("Penguin Random House")
            .address("Neumarkter Strasse 28")
            .state("Germany")
            .city("Munich")
            .zipCode("D-81673")
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
    void shouldGetAllPublishers() {
        var publisherDto = PUBLISHER_DTO_SUPPLIER.get();
        var createdPublisher = this.createPublisher(publisherDto);

        var result = this.getAllPublishers();

        assertThat(result)
                .isNotNull()
                .containsOnly(createdPublisher);
    }

    @Test
    @Rollback
    @Transactional
    void shouldCreateAndThenGetPublisherById() {
        var publisherDto = PUBLISHER_DTO_SUPPLIER.get();

        var publisherId = this.createPublisher(publisherDto).getId();
        var result = this.getPublisherById(publisherId);

        assertThat(result)
                .isNotNull()
                .matches(dto -> publisherId.equals(dto.getId()))
                .returns(publisherDto.getName(), PublisherDto::getName)
                .returns(publisherDto.getAddress(), PublisherDto::getAddress)
                .returns(publisherDto.getState(), PublisherDto::getState)
                .returns(publisherDto.getCity(), PublisherDto::getCity)
                .returns(publisherDto.getZipCode(), PublisherDto::getZipCode);
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
    void shouldFailToCreatePublisher(@NotNull Consumer<PublisherDto> publisherModifier) {
        var publisherDto = PUBLISHER_DTO_SUPPLIER.get();
        publisherModifier.accept(publisherDto);

        this.createPublisherAndExpect400(publisherDto);
    }

    @Test
    void shouldReturn404WhenPublisherIsNotFound() {
        var publisherId = UUID.randomUUID();
        this.getPublisherByIdAndExpect404(publisherId);
    }

    @Test
    @Rollback
    @Transactional
    void shouldCreatePublisher() {
        var publisherDto = PUBLISHER_DTO_SUPPLIER.get();

        var result = this.createPublisher(publisherDto);

        assertThat(result).isNotNull();
        assertAll(
                () -> assertThat(result)
                        .returns(publisherDto.getName(), PublisherDto::getName)
                        .returns(publisherDto.getAddress(), PublisherDto::getAddress)
                        .returns(publisherDto.getState(), PublisherDto::getState)
                        .returns(publisherDto.getCity(), PublisherDto::getCity)
                        .returns(publisherDto.getZipCode(), PublisherDto::getZipCode),
                () -> assertThat(result.getId())
                        .isNotNull()
        );
    }

    @Test
    @Rollback
    @Transactional
    void shouldUpdatePublisher() {
        var publisherDto = PUBLISHER_DTO_SUPPLIER.get();
        var createdPublisher = this.createPublisher(publisherDto);

        var result = this.updatePublisher(createdPublisher);

        assertThat(result)
                .isNotNull()
                .matches(dto -> dto.getId().equals(createdPublisher.getId()))
                .returns(publisherDto.getName(), PublisherDto::getName)
                .returns(publisherDto.getAddress(), PublisherDto::getAddress)
                .returns(publisherDto.getState(), PublisherDto::getState)
                .returns(publisherDto.getCity(), PublisherDto::getCity)
                .returns(publisherDto.getZipCode(), PublisherDto::getZipCode);
    }

    @Rollback
    @Transactional
    @ParameterizedTest
    @MethodSource("publisherModifiers")
    void shouldFailToUpdatePublisher(@NotNull Consumer<PublisherDto> publisherModifier) {
        var publisherDto = PUBLISHER_DTO_SUPPLIER.get();
        var createdPublisher = this.createPublisher(publisherDto);
        publisherModifier.accept(createdPublisher);

        this.updatePublisherAndExpect400(createdPublisher);
    }

    @Test
    void shouldReturn404WhenUpdatingPublisherIsNotFound() {
        var publisherDto = PUBLISHER_DTO_SUPPLIER.get();
        var publisherId = UUID.randomUUID();
        this.updatePublisherAndExpect404(publisherId, publisherDto);
    }

    @Test
    @Rollback
    @Transactional
    void shouldDeleteExistingPublisher() {
        var publisherDto = PUBLISHER_DTO_SUPPLIER.get();

        var publisherId = this.createPublisher(publisherDto).getId();
        this.deletePublisherById(publisherId);
    }

    @Test
    void shouldReturn404WhenDeletingPublisherIsNotFound() {
        var publisherId = UUID.randomUUID();
        this.deletePublisherAndExpect404(publisherId);
    }

    @SneakyThrows
    @NotNull
    private List<PublisherDto> getAllPublishers() {
        var strPublishers = this.mockMvc.perform(get("/publisher"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var arrayPublishers = this.objectMapper.readValue(strPublishers, PublisherDto[].class);
        return Arrays.asList(arrayPublishers);
    }

    @SneakyThrows
    @NotNull
    private PublisherDto createPublisher(@NotNull PublisherDto publisherDto) {
        var strPublisher = this.mockMvc.perform(post("/publisher")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(publisherDto))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(publisherDto.getName()))
                .andExpect(jsonPath("$.address").value(publisherDto.getAddress()))
                .andExpect(jsonPath("$.state").value(publisherDto.getState()))
                .andExpect(jsonPath("$.city").value(publisherDto.getCity()))
                .andExpect(jsonPath("$.zipCode").value(publisherDto.getZipCode()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strPublisher, PublisherDto.class);
    }

    @SneakyThrows
    private void createPublisherAndExpect400(@NotNull PublisherDto publisherDto) {
        this.mockMvc.perform(post("/publisher")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(publisherDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @NotNull
    private PublisherDto getPublisherById(@NotNull UUID publisherId) {
        var strPublisher = this.mockMvc.perform(get("/publisher/{id}", publisherId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(publisherId.toString()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strPublisher, PublisherDto.class);
    }

    @SneakyThrows
    private void getPublisherByIdAndExpect404(@NotNull UUID publisherId) {
        this.mockMvc.perform(get("/publisher/{id}", publisherId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @NotNull
    private PublisherDto updatePublisher(@NotNull PublisherDto publisherDto) {
        var strPublisher = this.mockMvc.perform(put("/publisher/{id}", publisherDto.getId())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(publisherDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(publisherDto.getId().toString()))
                .andExpect(jsonPath("$.name").value(publisherDto.getName()))
                .andExpect(jsonPath("$.address").value(publisherDto.getAddress()))
                .andExpect(jsonPath("$.state").value(publisherDto.getState()))
                .andExpect(jsonPath("$.city").value(publisherDto.getCity()))
                .andExpect(jsonPath("$.zipCode").value(publisherDto.getZipCode()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strPublisher, PublisherDto.class);
    }

    @SneakyThrows
    private void updatePublisherAndExpect400(@NotNull PublisherDto publisherDto) {
        this.mockMvc.perform(put("/publisher/{id}", publisherDto.getId())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(publisherDto))
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    private void updatePublisherAndExpect404(@NotNull UUID publisherId, @NotNull PublisherDto publisherDto) {
        this.mockMvc.perform(put("/publisher/{id}", publisherId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(publisherDto))
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    private void deletePublisherById(@NotNull UUID publisherId) {
        this.mockMvc.perform(delete("/publisher/{id}", publisherId))
                .andExpect(status().isNoContent());
    }

    @SneakyThrows
    private void deletePublisherAndExpect404(@NotNull UUID publisherId) {
        this.mockMvc.perform(delete("/publisher/{id}", publisherId))
                .andExpect(status().isNotFound());
    }

}
