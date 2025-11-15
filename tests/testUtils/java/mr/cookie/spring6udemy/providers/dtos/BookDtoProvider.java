package mr.cookie.spring6udemy.providers.dtos;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mr.cookie.spring6udemy.model.dtos.BookDto;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookDtoProvider {

    public static BookDto provideBookDtoWithIsbn(String isbn) {
        return provideBookDto(null, isbn);
    }

    public static BookDto provideBookDto() {
        var isbn = "%s-%s".formatted(randomNumeric(3), randomNumeric(10));
        return provideBookDto(null, isbn);
    }

    public static BookDto provideBookDto(UUID id) {
        var isbn = "%s-%s".formatted(randomNumeric(3), randomNumeric(10));
        return provideBookDto(id, isbn);
    }

    public static BookDto provideBookDto(UUID id, String isbn) {
        return BookDto.builder()
                .id(id)
                .title(randomAlphabetic(25))
                .isbn(isbn)
                .build();
    }

}
