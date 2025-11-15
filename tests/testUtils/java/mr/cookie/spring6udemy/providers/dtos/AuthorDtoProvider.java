package mr.cookie.spring6udemy.providers.dtos;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthorDtoProvider {

    public static AuthorDto provideAuthorDtoWithNames(String firstName, String lastName) {
        return AuthorDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    public static AuthorDto provideAuthorDto() {
        return provideAuthorDto(null);
    }

    public static AuthorDto provideAuthorDto(UUID id) {
        return AuthorDto.builder()
                .id(id)
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
    }

}
