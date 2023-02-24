package mr.cookie.spring6udemy.units.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import mr.cookie.spring6udemy.domain.Author;

class AuthorTest {

    @Test
    void authorsWithDifferentIdsAndTheSameNameHaveDifferentHashCode() {
        var author1 = buildAuthor(111L);
        var author2 = buildAuthor(42L);

        Assertions.assertThat(author1)
            .doesNotHaveSameHashCodeAs(author2);
    }

    @Test
    void authorsWithSameIdsButDifferentNamesHaveSameHashCode() {
        var author1 = buildAuthor(111L);
        var author2 = buildAuthor(111L);
        author2.setFirstName("CookieMr");

        Assertions.assertThat(author1)
            .hasSameHashCodeAs(author2);
    }

    @Test
    void authorsWithDifferentIdsAndTheSameNameAreNotEqual() {
        var author1 = buildAuthor(111L);
        var author2 = buildAuthor(42L);

        Assertions.assertThat(author1)
            .isNotEqualTo(author2);
    }

    @Test
    void authorsWithSameIdsButDifferentNamesAreEqual() {
        var author1 = buildAuthor(111L);
        var author2 = buildAuthor(111L);
        author2.setFirstName("CookieMr");

        Assertions.assertThat(author1)
            .isEqualTo(author2);
    }

    private Author buildAuthor(long id) {
        return Author.builder()
            .id(id)
            .firstName("Brandon")
            .lastName("Sanderson")
            .build();
    }

}
