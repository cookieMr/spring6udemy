package mr.cookie.spring6udemy.repositories;

import static mr.cookie.spring6udemy.providers.entities.AuthorEntityProvider.provideAuthorEntity;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles(profiles = {"test"})
class AuthorRepositoryIntegrationTest {

    @Autowired
    private AuthorRepository repository;

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void shouldFindByFirstNameAndLastName() {
        var authorEntity = provideAuthorEntity();
        repository.save(authorEntity);

        var result = repository.findByFirstNameAndLastName(
                authorEntity.getFirstName(),
                authorEntity.getLastName());

        assertThat(result)
                .isNotEmpty()
                .contains(authorEntity);
    }

    @Test
    void shouldNotFindByFirstNameAndLastName() {
        var result = repository.findByFirstNameAndLastName(
                randomAlphabetic(25),
                randomAlphabetic(25));

        assertThat(result)
                .isEmpty();
    }

}
