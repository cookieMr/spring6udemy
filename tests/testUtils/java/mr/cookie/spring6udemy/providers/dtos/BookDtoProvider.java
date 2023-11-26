package mr.cookie.spring6udemy.providers.dtos;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

import lombok.experimental.UtilityClass;
import mr.cookie.spring6udemy.model.dtos.BookDto;

import java.util.UUID;

@UtilityClass
public class BookDtoProvider {

    public BookDto provideBookDtoWithIsbn(String isbn) {
        return provideBookDto(null, isbn);
    }

    public BookDto provideBookDto() {
        var isbn = "%s-%s".formatted(randomNumeric(3), randomNumeric(10));
        return provideBookDto(null, isbn);
    }

    public BookDto provideBookDto(UUID id) {
        var isbn = "%s-%s".formatted(randomNumeric(3), randomNumeric(10));
        return provideBookDto(id, isbn);
    }

    public BookDto provideBookDto(UUID id, String isbn) {
        return BookDto.builder()
                .id(id)
                .title(randomAlphabetic(25))
                .isbn(isbn)
                .build();
    }

}
