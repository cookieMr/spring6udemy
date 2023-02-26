package mr.cookie.spring6udemy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mr.cookie.spring6udemy.model.model.Publisher;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PublisherControllerTest {

    private static final long PUBLISHER_ID = 1L;
    private static final Publisher EXPECTED_PUBLISHER = Publisher.builder()
            .id(PUBLISHER_ID)
            .name("DragonSteel Books")
            .address("PO Box 698")
            .state("UT")
            .city("American Fork")
            .zipCode("84003")
            .build();

    @NotNull
    @Autowired
    private ObjectMapper objectMapper;

    @NotNull
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllPublishers() throws Exception {
        this.mockMvc.perform(get("/publisher"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value(EXPECTED_PUBLISHER.getName()))
                .andExpect(jsonPath("$[0].address").value(EXPECTED_PUBLISHER.getAddress()))
                .andExpect(jsonPath("$[0].state").value(EXPECTED_PUBLISHER.getState()))
                .andExpect(jsonPath("$[0].city").value(EXPECTED_PUBLISHER.getCity()))
                .andExpect(jsonPath("$[0].zipCode").value(EXPECTED_PUBLISHER.getZipCode()));
    }

    @Test
    void getPublisherById() {
        var publisher = Publisher.builder()
                .name("Penguin Random House")
                .address("Neumarkter Strasse 28")
                .state("Germany")
                .city("Munich")
                .zipCode("D-81673")
                .build();

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
    void postPublisherToCreate() {
        var publisher = Publisher.builder()
                .name("Penguin Random House")
                .address("Neumarkter Strasse 28")
                .state("Germany")
                .city("Munich")
                .zipCode("D-81673")
                .build();

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

    @SneakyThrows
    private Publisher createPublisher(Publisher publisher) {
        var strAuthor = this.mockMvc.perform(post("/publisher")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(publisher))
                )
                .andExpect(status().isOk())
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

        return this.objectMapper.readValue(strAuthor, Publisher.class);
    }

    @SneakyThrows
    private Publisher getPublisherById(long publisherId) {
        var strAuthor = this.mockMvc.perform(get("/publisher/{id}", publisherId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(publisherId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return this.objectMapper.readValue(strAuthor, Publisher.class);
    }

}
