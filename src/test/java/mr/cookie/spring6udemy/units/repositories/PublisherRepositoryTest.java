package mr.cookie.spring6udemy.units.repositories;

import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import mr.cookie.spring6udemy.repositories.PublisherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PublisherRepositoryTest {

    @Autowired
    private PublisherRepository publisherRepository;

    @Test
    void savePublisher() {
        var publisher = PublisherEntity.builder()
                .name("DragonSteel Books")
                .address("PO Box 698")
                .city("American Fork")
                .state("UT")
                .zipCode("84003")
                .build();

        var result = this.publisherRepository.save(publisher);

        assertThat(result)
                .isNotNull()
                .isSameAs(publisher);
        assertThat(result.getId())
                .isNotNull();
    }

}
