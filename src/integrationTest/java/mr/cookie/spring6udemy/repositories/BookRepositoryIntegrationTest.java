package mr.cookie.spring6udemy.repositories;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;

import mr.cookie.spring6udemy.model.entities.BookEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ActiveProfiles(profiles = {"test"})
@ExtendWith(SpringExtension.class)
class BookRepositoryIntegrationTest {

    @Autowired
    private BookRepository repository;

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void shouldFindByIsbn() {
        var bookEntity = BookEntity.builder()
                .title(randomAlphabetic(25))
                .isbn(randomAlphabetic(25))
                .build();
        repository.save(bookEntity);

        var result = repository.findByIsbn(bookEntity.getIsbn());

        assertThat(result)
                .isNotEmpty()
                .contains(bookEntity);
    }

    @Test
    void shouldNotFindByIsn() {
        var result = repository.findByIsbn(randomAlphabetic(25));

        assertThat(result)
                .isEmpty();
    }

}
