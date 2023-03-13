package mr.cookie.spring6udemy.units.repositories;

import mr.cookie.spring6udemy.model.entities.BookEntity;
import mr.cookie.spring6udemy.repositories.BookRepository;
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

        assertThatThrownBy(() -> {
            this.bookRepository.save(book);
            this.bookRepository.flush();
        })
                .isNotNull()
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("could not execute statement; SQL [n/a]; constraint [null]");
    }

}
