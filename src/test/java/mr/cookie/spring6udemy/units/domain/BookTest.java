package mr.cookie.spring6udemy.units.domain;

import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import mr.cookie.spring6udemy.model.entities.BookDto;

class BookTest {

    @Test
    void booksWithDifferentIdsAndTheSameTitleHaveDifferentHashCode() {
        var book1 = buildBook(111L);
        var book2 = buildBook(42L);

        Assertions.assertThat(book1)
            .doesNotHaveSameHashCodeAs(book2);
    }

    @Test
    void booksWithSameIdsButDifferentTitlesHaveSameHashCode() {
        var book1 = buildBook(111L);
        var book2 = buildBook(111L);
        book2.setIsbn("1250899656");

        Assertions.assertThat(book1)
            .hasSameHashCodeAs(book2);
    }

    @Test
    void booksWithDifferentIdsAndTheSameTitleAreNotEqual() {
        var book1 = buildBook(111L);
        var book2 = buildBook(42L);

        Assertions.assertThat(book1)
            .isNotEqualTo(book2);
    }

    @Test
    void booksWithSameIdsButDifferentTitlesAreEqual() {
        var book1 = buildBook(111L);
        var book2 = buildBook(111L);
        book2.setIsbn("1250899656");

        Assertions.assertThat(book1)
            .isEqualTo(book2);
    }

    @NotNull
    private BookDto buildBook(long id) {
        return BookDto.builder()
            .id(id)
            .title("Tress of Emerald See")
            .isbn("978-1250899651")
            .build();
    }

}
