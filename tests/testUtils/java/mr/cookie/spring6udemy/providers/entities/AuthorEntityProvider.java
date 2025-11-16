package mr.cookie.spring6udemy.providers.entities;

import static org.apache.commons.lang3.RandomStringUtils.secure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthorEntityProvider {

    @NotNull
    public static AuthorEntity provideAuthorEntity() {
        return provideAuthorEntity(null);
    }

    @NotNull
    public static AuthorEntity provideAuthorEntity(UUID id) {
        return AuthorEntity.builder()
                .id(id)
                .firstName(secure().nextAlphabetic(25))
                .lastName(secure().nextAlphabetic(25))
                .build();
    }

}
