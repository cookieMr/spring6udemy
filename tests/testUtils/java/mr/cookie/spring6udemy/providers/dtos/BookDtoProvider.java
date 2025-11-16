package mr.cookie.spring6udemy.providers.dtos;

import static org.apache.commons.lang3.RandomStringUtils.secure;

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
        var isbn = "%s-%s".formatted(secure().nextNumeric(3), secure().nextNumeric(10));
        return provideBookDto(null, isbn);
    }

    public static BookDto provideBookDto(UUID id) {
        var isbn = "%s-%s".formatted(secure().nextNumeric(3), secure().nextNumeric(10));
        return provideBookDto(id, isbn);
    }

    public static BookDto provideBookDto(UUID id, String isbn) {
        return BookDto.builder()
                .id(id)
                .title(secure().nextAlphabetic(25))
                .isbn(isbn)
                .build();
    }

}
