package mr.cookie.spring6udemy.providers.dtos;

import static org.apache.commons.lang3.RandomStringUtils.secure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mr.cookie.spring6udemy.model.dtos.PublisherDto;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PublisherDtoProvider {

    public static PublisherDto providePublisherDtoWithName(String name) {
        return providePublisherDto(null, name);
    }

    public static PublisherDto providePublisherDto() {
        return providePublisherDto(null, secure().nextAlphabetic(25));
    }

    public static PublisherDto providePublisherDto(UUID id) {
        return providePublisherDto(id, secure().nextAlphabetic(25));
    }

    public static PublisherDto providePublisherDto(UUID id, String name) {
        return PublisherDto.builder()
                .id(id)
                .name(name)
                .address(secure().nextAlphabetic(25))
                .state(secure().nextAlphabetic(25))
                .city(secure().nextAlphabetic(25))
                .zipCode(secure().nextAlphabetic(25))
                .build();
    }

}
