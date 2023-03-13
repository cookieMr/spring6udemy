package mr.cookie.spring6udemy.units.repositories;

import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
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

        var result = this.authorRepository.save(author);

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

        assertThatThrownBy(() -> {
            this.authorRepository.save(author);
            this.authorRepository.flush();
        })
                .isNotNull()
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("could not execute statement; SQL [n/a]; constraint [null]");
    }

}
