package mr.cookie.spring6udemy.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles(profiles = {"test"})
class AuthorRepositoryTest {

    private static final Supplier<AuthorEntity> AUTHOR_SUPPLIER = () -> AuthorEntity.builder()
            .firstName("Brandon")
            .lastName("Sanderson")
            .build();

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void saveAuthor() {
        var author = AUTHOR_SUPPLIER.get();

        var result = authorRepository.save(author);

        assertThat(result)
                .isNotNull()
                .isSameAs(author);
        assertThat(result.getId())
                .isNotNull();
    }

    static Stream<Consumer<AuthorEntity>> consumerWithExpectedErrorMessage() {
        return Stream.of(
                author -> author.setFirstName(null),
                author -> author.setLastName(null)
        );
    }

    @ParameterizedTest
    @MethodSource("consumerWithExpectedErrorMessage")
    void saveAuthorShouldFailValidation(Consumer<AuthorEntity> authorModifier) {
        var author = AUTHOR_SUPPLIER.get();
        authorModifier.accept(author);

        authorRepository.save(author);
        assertThatThrownBy(authorRepository::flush)
                .isNotNull()
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("could not execute statement [NULL not allowed for column");
    }

}
