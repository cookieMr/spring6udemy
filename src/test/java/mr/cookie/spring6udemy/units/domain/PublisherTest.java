package mr.cookie.spring6udemy.units.domain;

import mr.cookie.spring6udemy.model.entities.PublisherDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PublisherTest {

    @Test
    void publishersWithDifferentIdsAndTheSameStateHaveDifferentHashCode() {
        var publisher1 = buildPublisher(111L);
        var publisher2 = buildPublisher(42L);

        Assertions.assertThat(publisher1)
                .doesNotHaveSameHashCodeAs(publisher2);
    }

    @Test
    void publishersWithSameIdsButDifferentStateHaveSameHashCode() {
        var publisher1 = buildPublisher(111L);
        var publisher2 = buildPublisher(111L);
        publisher2.setState("FL");

        Assertions.assertThat(publisher1)
                .hasSameHashCodeAs(publisher2);
    }

    @Test
    void publishersWithDifferentIdsAndTheSameStateAreNotEqual() {
        var publisher1 = buildPublisher(111L);
        var publisher2 = buildPublisher(42L);

        Assertions.assertThat(publisher1)
                .isNotEqualTo(publisher2);
    }

    @Test
    void publishersWithSameIdsButDifferentStateAreEqual() {
        var publisher1 = buildPublisher(111L);
        var publisher2 = buildPublisher(111L);
        publisher2.setState("FL");

        Assertions.assertThat(publisher1)
                .isEqualTo(publisher2);
    }

    private PublisherDto buildPublisher(long id) {
        return PublisherDto.builder()
                .id(id)
                .name("DragonSteel Books")
                .address("POBox 698")
                .state("UT")
                .city("American Fork")
                .zipCode("84003")
                .build();
    }

}
