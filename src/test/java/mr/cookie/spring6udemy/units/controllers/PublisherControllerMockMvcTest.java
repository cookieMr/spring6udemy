package mr.cookie.spring6udemy.units.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.controllers.PublisherController;
import mr.cookie.spring6udemy.model.model.Publisher;
import mr.cookie.spring6udemy.services.PublisherService;
import mr.cookie.spring6udemy.exceptions.NotFoundEntityException;
import org.hamcrest.core.Is;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
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
@WebMvcTest(PublisherController.class)
class PublisherControllerMockMvcTest {

    private static final long PUBLISHER_ID = 1L;
    private static final Publisher PUBLISHER = Publisher.builder()
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

    @NotNull
    @MockBean
    private PublisherService publisherService;

    @Test
    void shouldGetAllPublishers() {
        given(this.publisherService.findAll()).willReturn(Collections.singletonList(PUBLISHER));

        var result = this.getAllPublishers();

        assertThat(result)
                .isNotNull()
                .containsOnly(PUBLISHER);

        verify(this.publisherService).findAll();
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldGetPublisherById() {
        given(this.publisherService.findById(anyLong())).willReturn(Optional.of(PUBLISHER));

        var result = this.getPublisherById(PUBLISHER_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(PUBLISHER);

        verify(this.publisherService).findById(PUBLISHER_ID);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldGet404WhenCannotFindPublisherById() {
        given(this.publisherService.findById(anyLong()))
                .willThrow(new NotFoundEntityException(PUBLISHER_ID, Publisher.class));

        this.getPublisherByIdAndExpect404(PUBLISHER_ID);

        verify(this.publisherService).findById(PUBLISHER_ID);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldCreatePublisher() {
        given(this.publisherService.create(any(Publisher.class))).willReturn(PUBLISHER);

        var result = this.createPublisher(PUBLISHER);

        assertThat(result)
                .isNotNull()
                .isEqualTo(PUBLISHER);

        verify(this.publisherService).create(PUBLISHER);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldUpdatePublisher() {
        given(this.publisherService.update(anyLong(), any(Publisher.class))).willReturn(PUBLISHER);

        var result = this.updatePublisher(PUBLISHER_ID, PUBLISHER);

        assertThat(result)
                .isNotNull()
                .isEqualTo(PUBLISHER);

        verify(this.publisherService).update(PUBLISHER_ID, PUBLISHER);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldGet404WhenCannotUpdatePublisherById() {
        given(this.publisherService.update(anyLong(), any(Publisher.class)))
                .willThrow(new NotFoundEntityException(PUBLISHER_ID, Publisher.class));

        this.updatePublisherAndExpect404(PUBLISHER_ID, PUBLISHER);

        verify(this.publisherService).update(PUBLISHER_ID, PUBLISHER);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldDeletePublisher() {
        this.deletePublisherById(PUBLISHER_ID);

        verify(this.publisherService).deleteById(PUBLISHER_ID);
        verifyNoMoreInteractions(this.publisherService);
    }

    @Test
    void shouldGet404WhenCannotDeletePublisherById() {
        doThrow(new NotFoundEntityException(PUBLISHER_ID, Publisher.class))
                .when(this.publisherService)
                .deleteById(PUBLISHER_ID);

        this.deletePublisherAndExpect404(PUBLISHER_ID);

        verify(this.publisherService).deleteById(PUBLISHER_ID);
        verifyNoMoreInteractions(this.publisherService);
    }

    @SneakyThrows
    @NotNull
    private List<Publisher> getAllPublishers() {
        var strPublishers = this.mockMvc.perform(get("/publisher"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", Is.is(1)))
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
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strPublisher, Publisher.class);
    }

    @SneakyThrows
    private void getPublisherByIdAndExpect404(long publisherId) {
        this.mockMvc.perform(get("/publisher/{id}", publisherId))
                .andExpect(status().isNotFound());
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
    private void updatePublisherAndExpect404(long publisherId, @NotNull Publisher publisher) {
        this.mockMvc.perform(put("/publisher/{id}", publisherId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(publisher))
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    private void deletePublisherById(long publisherId) {
        this.mockMvc.perform(delete("/publisher/{id}", publisherId))
                .andExpect(status().isNoContent());
    }

    @SneakyThrows
    private void deletePublisherAndExpect404(long publisherId) {
        this.mockMvc.perform(delete("/publisher/{id}", publisherId))
                .andExpect(status().isNotFound());
    }

}
