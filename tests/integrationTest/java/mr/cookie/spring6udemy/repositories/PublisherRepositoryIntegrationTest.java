package mr.cookie.spring6udemy.repositories;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;

import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles(profiles = {"test"})
class PublisherRepositoryIntegrationTest {

    @Autowired
    private PublisherRepository repository;

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void shouldFindByName() {
        var publisherEntity = PublisherEntity.builder()
                .name(randomAlphabetic(25))
                .address(randomAlphabetic(25))
                .state(randomAlphabetic(25))
                .city(randomAlphabetic(25))
                .zipCode(randomAlphabetic(25))
                .build();
        repository.save(publisherEntity);

        var result = repository.findByName(publisherEntity.getName());

        assertThat(result)
                .isNotEmpty()
                .contains(publisherEntity);
    }

    @Test
    void shouldNotFindByName() {
        var result = repository.findByName(randomAlphabetic(25));

        assertThat(result)
                .isEmpty();
    }

}
