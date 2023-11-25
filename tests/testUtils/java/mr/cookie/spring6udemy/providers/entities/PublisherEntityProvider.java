package mr.cookie.spring6udemy.providers.entities;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import java.util.UUID;
import lombok.experimental.UtilityClass;
import mr.cookie.spring6udemy.model.entities.PublisherEntity;

@UtilityClass
public class PublisherEntityProvider {

    public PublisherEntity providePublisherEntity() {
        return providePublisherEntity(null);
    }

    public PublisherEntity providePublisherEntity(UUID id) {
        return PublisherEntity.builder()
                .id(id)
                .name(randomAlphabetic(25))
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();
    }

}
