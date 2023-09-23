package mr.cookie.spring6udemy.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import mr.cookie.spring6udemy.model.entities.BookEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles(profiles = {"test"})
class BookRepositoryTest {

    private static final Supplier<BookEntity> BOOK_SUPPLIER = () -> BookEntity.builder()
            .title("Warbreaker")
            .isbn("978-0765360038")
            .build();

    @Autowired
    private BookRepository bookRepository;

    @Test
    void saveBook() {
        var book = BOOK_SUPPLIER.get();

        var result = this.bookRepository.save(book);

        assertThat(result)
                .isNotNull()
                .isSameAs(book);
        assertThat(result.getId())
                .isNotNull();
    }

    static Stream<Consumer<BookEntity>> consumerWithExpectedErrorMessage() {
        return Stream.of(
                book -> book.setTitle(null),
                book -> book.setIsbn(null)
        );
    }

    @ParameterizedTest
    @MethodSource("consumerWithExpectedErrorMessage")
    void saveBookShouldFailValidation(Consumer<BookEntity> bookModifier) {
        var book = BOOK_SUPPLIER.get();
        bookModifier.accept(book);

        this.bookRepository.save(book);
        assertThatThrownBy(this.bookRepository::flush)
                .isNotNull()
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("could not execute statement [NULL not allowed for column");
    }

}
