package mr.cookie.spring6udemy.domain;

import java.util.UUID;
import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

class AuthorEntityTest {

    @Test
    void authorsWithDifferentIdsAndTheSameNameHaveDifferentHashCode() {
        var author1 = buildAuthor(UUID.randomUUID());
        var author2 = buildAuthor(UUID.randomUUID());

        Assertions.assertThat(author1)
                .doesNotHaveSameHashCodeAs(author2);
    }

    @Test
    void authorsWithSameIdsButDifferentNamesHaveSameHashCode() {
        var id = UUID.randomUUID();

        var author1 = buildAuthor(id);
        var author2 = buildAuthor(id)
                .setFirstName("CookieMr");

        Assertions.assertThat(author1)
                .hasSameHashCodeAs(author2);
    }

    @Test
    void authorsWithDifferentIdsAndTheSameNameAreNotEqual() {
        var author1 = buildAuthor(UUID.randomUUID());
        var author2 = buildAuthor(UUID.randomUUID());

        Assertions.assertThat(author1)
                .isNotEqualTo(author2);
    }

    @Test
    void authorsWithSameIdsButDifferentNamesAreEqual() {
        var id = UUID.randomUUID();

        var author1 = buildAuthor(id);
        var author2 = buildAuthor(id)
                .setFirstName("CookieMr");

        Assertions.assertThat(author1)
                .isEqualTo(author2);
    }

    @NotNull
    private AuthorEntity buildAuthor(@NotNull UUID id) {
        return AuthorEntity.builder()
                .id(id)
                .firstName("Brandon")
                .lastName("Sanderson")
                .build();
    }

}
