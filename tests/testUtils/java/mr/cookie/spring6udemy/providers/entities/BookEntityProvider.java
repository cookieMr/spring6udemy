package mr.cookie.spring6udemy.providers.entities;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mr.cookie.spring6udemy.model.entities.BookEntity;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookEntityProvider {

    public static BookEntity provideBookEntity() {
        return provideBookEntity(null);
    }

    public static BookEntity provideBookEntity(UUID id) {
        return BookEntity.builder()
                .id(id)
                .title(randomAlphabetic(25))
                .isbn("%s-%s".formatted(randomNumeric(3), randomNumeric(10)))
                .build();
    }

}
