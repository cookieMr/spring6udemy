package mr.cookie.spring6udemy.units.repositories;

import mr.cookie.spring6udemy.model.entities.BookEntity;
import mr.cookie.spring6udemy.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void saveBook() {
        var book = BookEntity.builder()
                .title("Warbreaker")
                .isbn("978-0765360038")
                .build();

        var result = this.bookRepository.save(book);

        assertThat(result)
                .isNotNull()
                .isSameAs(book);
        assertThat(result.getId())
                .isNotNull();
    }

}
