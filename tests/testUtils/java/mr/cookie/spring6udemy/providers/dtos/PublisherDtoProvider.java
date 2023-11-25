package mr.cookie.spring6udemy.providers.dtos;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import java.util.UUID;
import lombok.experimental.UtilityClass;
import mr.cookie.spring6udemy.model.dtos.PublisherDto;

@UtilityClass
public class PublisherDtoProvider {

    public PublisherDto providePublisherDtoWithName(String name) {
        return providePublisherDto(null, name);
    }

    public PublisherDto providePublisherDto() {
        return providePublisherDto(null, randomAlphabetic(25));
    }

    public PublisherDto providePublisherDto(UUID id) {
        return providePublisherDto(id, randomAlphabetic(25));
    }

    public PublisherDto providePublisherDto(UUID id, String name) {
        return PublisherDto.builder()
                .id(id)
                .name(name)
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();
    }

}
