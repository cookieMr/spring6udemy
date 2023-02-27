package mr.cookie.spring6udemy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.model.model.Publisher;
import org.hamcrest.core.Is;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

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

    private static final long PUBLISHER_ID = 1L;
    private static final Supplier<Publisher> PUBLISHER_SUPPLIER = () -> Publisher.builder()
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
    void shouldGetAllPublishers() {
        var result = this.getAllPublishers();

        assertThat(result)
                .isNotNull()
                .isNotEmpty();
        // TODO: should contain a publisher
    }

    @Test
    void shouldCreateAndThenGetPublisherById() {
        var publisher = PUBLISHER_SUPPLIER.get();

        var publisherId = this.createPublisher(publisher).getId();
        var result = this.getPublisherById(publisherId);

        assertThat(result)
                .isNotNull()
                .returns(publisherId, Publisher::getId)
                .returns(publisher.getName(), Publisher::getName)
                .returns(publisher.getAddress(), Publisher::getAddress)
                .returns(publisher.getState(), Publisher::getState)
                .returns(publisher.getCity(), Publisher::getCity)
                .returns(publisher.getZipCode(), Publisher::getZipCode);
    }

    @Disabled
    @Test
    void shouldReturn404WhenPublisherIsNotFound() throws Exception {
        // TODO: error handling
        this.mockMvc.perform(get("/publisher/%s".formatted(Integer.MAX_VALUE)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreatePublisher() {
        var publisher = PUBLISHER_SUPPLIER.get();

        var result = this.createPublisher(publisher);

        assertThat(result).isNotNull();
        assertAll(
                () -> assertThat(result)
                        .returns(publisher.getName(), Publisher::getName)
                        .returns(publisher.getAddress(), Publisher::getAddress)
                        .returns(publisher.getState(), Publisher::getState)
                        .returns(publisher.getCity(), Publisher::getCity)
                        .returns(publisher.getZipCode(), Publisher::getZipCode),
                () -> assertThat(result.getId())
                        .isNotNull()
                        .isPositive()
        );
    }

    @Test
    void shouldUpdatePublisher() {
        var publisher = PUBLISHER_SUPPLIER.get();

        var result = this.updatePublisher(PUBLISHER_ID, publisher);

        assertThat(result).isNotNull();
        assertThat(result)
                .returns(PUBLISHER_ID, Publisher::getId)
                .returns(publisher.getName(), Publisher::getName)
                .returns(publisher.getAddress(), Publisher::getAddress)
                .returns(publisher.getState(), Publisher::getState)
                .returns(publisher.getCity(), Publisher::getCity)
                .returns(publisher.getZipCode(), Publisher::getZipCode);
    }

    @Test
    void shouldDeleteExistingPublisher() {
        var publisher = PUBLISHER_SUPPLIER.get();

        var publisherId = this.createPublisher(publisher).getId();
        this.deletePublisherById(publisherId);
    }

    @SneakyThrows
    @NotNull
    private List<Publisher> getAllPublishers() {
        var strPublishers = this.mockMvc.perform(get("/publisher"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var arrayPublishers = this.objectMapper.readValue(strPublishers, Publisher[].class);
        return Arrays.asList(arrayPublishers);
    }

    @SneakyThrows
    @NotNull
    private Publisher createPublisher(@NotNull Publisher publisher) {
        var strPublisher = this.mockMvc.perform(post("/publisher")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(publisher))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(publisher.getName()))
                .andExpect(jsonPath("$.address").value(publisher.getAddress()))
                .andExpect(jsonPath("$.state").value(publisher.getState()))
                .andExpect(jsonPath("$.city").value(publisher.getCity()))
                .andExpect(jsonPath("$.zipCode").value(publisher.getZipCode()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strPublisher, Publisher.class);
    }

    @SneakyThrows
    @NotNull
    private Publisher getPublisherById(long publisherId) {
        var strPublisher = this.mockMvc.perform(get("/publisher/{id}", publisherId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(publisherId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strPublisher, Publisher.class);
    }

    @SneakyThrows
    @NotNull
    private Publisher updatePublisher(long publisherId, @NotNull Publisher publisher) {
        var strPublisher = this.mockMvc.perform(put("/publisher/{id}", publisherId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(publisher))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id", Is.is(publisherId), Long.class))
                .andExpect(jsonPath("$.name").value(publisher.getName()))
                .andExpect(jsonPath("$.address").value(publisher.getAddress()))
                .andExpect(jsonPath("$.state").value(publisher.getState()))
                .andExpect(jsonPath("$.city").value(publisher.getCity()))
                .andExpect(jsonPath("$.zipCode").value(publisher.getZipCode()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strPublisher, Publisher.class);
    }

    @SneakyThrows
    private void deletePublisherById(long publisherId) {
        this.mockMvc.perform(delete("/publisher/{id}", publisherId))
                .andExpect(status().isNoContent());
    }

}
