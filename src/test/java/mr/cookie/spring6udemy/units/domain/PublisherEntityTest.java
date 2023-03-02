package mr.cookie.spring6udemy.units.domain;

import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

class PublisherEntityTest {

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

    @NotNull
    private PublisherEntity buildPublisher(long id) {
        return PublisherEntity.builder()
                .id(id)
                .name("DragonSteel Books")
                .address("PO Box 698")
                .state("UT")
                .city("American Fork")
                .zipCode("84003")
                .build();
    }

}
