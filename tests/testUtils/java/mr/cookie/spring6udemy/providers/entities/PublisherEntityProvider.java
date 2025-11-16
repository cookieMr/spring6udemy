package mr.cookie.spring6udemy.providers.entities;

import static org.apache.commons.lang3.RandomStringUtils.secure;

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
                .name(secure().nextAlphabetic(25))
                .address(secure().nextAlphabetic(25))
                .state(secure().nextAlphabetic(25))
                .city(secure().nextAlphabetic(25))
                .zipCode(secure().nextAlphabetic(25))
                .build();
    }

}
