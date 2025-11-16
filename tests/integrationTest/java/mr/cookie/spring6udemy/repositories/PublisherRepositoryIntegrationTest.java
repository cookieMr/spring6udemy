package mr.cookie.spring6udemy.repositories;

import static mr.cookie.spring6udemy.providers.entities.PublisherEntityProvider.providePublisherEntity;
import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.assertj.core.api.Assertions.assertThat;

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
        var publisherEntity = providePublisherEntity();
        repository.save(publisherEntity);

        var result = repository.findByName(publisherEntity.getName());

        assertThat(result)
                .isNotEmpty()
                .contains(publisherEntity);
    }

    @Test
    void shouldNotFindByName() {
        var result = repository.findByName(secure().nextAlphabetic(25));

        assertThat(result)
                .isEmpty();
    }

}
