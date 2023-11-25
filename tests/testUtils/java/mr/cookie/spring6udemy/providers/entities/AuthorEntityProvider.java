package mr.cookie.spring6udemy.providers.entities;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import java.util.UUID;
import lombok.experimental.UtilityClass;
import mr.cookie.spring6udemy.model.entities.AuthorEntity;

@UtilityClass
public class AuthorEntityProvider {

    public AuthorEntity provideAuthorEntity() {
        return provideAuthorEntity(null);
    }

    public AuthorEntity provideAuthorEntity(UUID id) {
        return AuthorEntity.builder()
                .id(id)
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
    }

}
