package mr.cookie.spring6udemy.providers.entities;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mr.cookie.spring6udemy.model.entities.AuthorEntity;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthorEntityProvider {

    public static AuthorEntity provideAuthorEntity() {
        return provideAuthorEntity(null);
    }

    public static AuthorEntity provideAuthorEntity(UUID id) {
        return AuthorEntity.builder()
                .id(id)
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
    }

}
