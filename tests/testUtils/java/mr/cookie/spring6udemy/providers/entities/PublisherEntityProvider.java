package mr.cookie.spring6udemy.providers.entities;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mr.cookie.spring6udemy.model.entities.PublisherEntity;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PublisherEntityProvider {

    public static PublisherEntity providePublisherEntity() {
        return providePublisherEntity(null);
    }

    public static PublisherEntity providePublisherEntity(UUID id) {
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
