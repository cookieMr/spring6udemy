package mr.cookie.spring6udemy.services;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private MockMvc mockMvc;

    @Test
    void getAllPublishers() throws Exception {
        this.mockMvc.perform(get("/publisher"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].name").value(EXPECTED_PUBLISHER.getName()))
                .andExpect(jsonPath("$[0].address").value(EXPECTED_PUBLISHER.getAddress()))
                .andExpect(jsonPath("$[0].state").value(EXPECTED_PUBLISHER.getState()))
                .andExpect(jsonPath("$[0].city").value(EXPECTED_PUBLISHER.getCity()))
                .andExpect(jsonPath("$[0].zipCode").value(EXPECTED_PUBLISHER.getZipCode()));
    }

    @Test
    void getAuthorById() throws Exception {
        this.mockMvc.perform(get("/publisher/%s".formatted(PUBLISHER_ID)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value(EXPECTED_PUBLISHER.getName()))
                .andExpect(jsonPath("$.address").value(EXPECTED_PUBLISHER.getAddress()))
                .andExpect(jsonPath("$.state").value(EXPECTED_PUBLISHER.getState()))
                .andExpect(jsonPath("$.city").value(EXPECTED_PUBLISHER.getCity()))
                .andExpect(jsonPath("$.zipCode").value(EXPECTED_PUBLISHER.getZipCode()));
    }

    @Disabled
    @Test
    void shouldReturn404WhenPublisherIsNotFound() throws Exception {
        // TODO: error handling
        this.mockMvc.perform(get("/publisher/%s".formatted(Integer.MAX_VALUE)))
                .andExpect(status().isNotFound());
    }

}
