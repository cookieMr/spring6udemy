package mr.cookie.spring6udemy.providers.dtos;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import lombok.experimental.UtilityClass;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;

import java.util.UUID;

@UtilityClass
public class AuthorDtoProvider {

    public AuthorDto provideAuthorDtoWithNames(String firstName, String lastName) {
        return AuthorDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    public AuthorDto provideAuthorDto() {
        return provideAuthorDto(null);
    }

    public AuthorDto provideAuthorDto(UUID id) {
        return AuthorDto.builder()
                .id(id)
                .firstName(randomAlphabetic(25))
                .lastName(randomAlphabetic(25))
                .build();
    }

}
