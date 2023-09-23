package mr.cookie.spring6udemy.domain;

import mr.cookie.spring6udemy.model.entities.BookEntity;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class BookEntityTest {

    @Test
    void booksWithDifferentIdsAndTheSameTitleHaveDifferentHashCode() {
        var book1 = buildBook(UUID.randomUUID());
        var book2 = buildBook(UUID.randomUUID());

        Assertions.assertThat(book1)
            .doesNotHaveSameHashCodeAs(book2);
    }

    @Test
    void booksWithSameIdsButDifferentTitlesHaveSameHashCode() {
        var id = UUID.randomUUID();

        var book1 = buildBook(id);
        var book2 = buildBook(id);
        book2.setIsbn("1250899656");

        Assertions.assertThat(book1)
            .hasSameHashCodeAs(book2);
    }

    @Test
    void booksWithDifferentIdsAndTheSameTitleAreNotEqual() {
        var book1 = buildBook(UUID.randomUUID());
        var book2 = buildBook(UUID.randomUUID());

        Assertions.assertThat(book1)
            .isNotEqualTo(book2);
    }

    @Test
    void booksWithSameIdsButDifferentTitlesAreEqual() {
        var id = UUID.randomUUID();

        var book1 = buildBook(id);
        var book2 = buildBook(id);
        book2.setIsbn("1250899656");

        Assertions.assertThat(book1)
            .isEqualTo(book2);
    }

    @NotNull
    private BookEntity buildBook(@NotNull UUID id) {
        return BookEntity.builder()
            .id(id)
            .title("Tress of Emerald See")
            .isbn("978-1250899651")
            .build();
    }

}
