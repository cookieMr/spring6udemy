package mr.cookie.spring6udemy.repositories;

import static mr.cookie.spring6udemy.providers.entities.BookEntityProvider.provideBookEntity;
import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles(profiles = {"test"})
class BookRepositoryIntegrationTest {

    @Autowired
    private BookRepository repository;

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void shouldFindByIsbn() {
        var bookEntity = provideBookEntity();
        repository.save(bookEntity);

        var result = repository.findByIsbn(bookEntity.getIsbn());

        assertThat(result)
                .isNotEmpty()
                .contains(bookEntity);
    }

    @Test
    void shouldNotFindByIsbn() {
        var result = repository.findByIsbn(secure().nextAlphabetic(25));

        assertThat(result)
                .isEmpty();
    }

}
