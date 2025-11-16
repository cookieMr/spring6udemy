package mr.cookie.spring6udemy.providers.entities;

import static org.apache.commons.lang3.RandomStringUtils.secure;

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
                .title(secure().nextAlphabetic(25))
                .isbn("%s-%s".formatted(secure().nextNumeric(3), secure().nextNumeric(10)))
                .build();
    }

}
