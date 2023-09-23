package mr.cookie.spring6udemy.domain;

import java.util.UUID;
import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

class PublisherEntityTest {

    @Test
    void publishersWithDifferentIdsAndTheSameStateHaveDifferentHashCode() {
        var publisher1 = buildPublisher(UUID.randomUUID());
        var publisher2 = buildPublisher(UUID.randomUUID());

        Assertions.assertThat(publisher1)
                .doesNotHaveSameHashCodeAs(publisher2);
    }

    @Test
    void publishersWithSameIdsButDifferentStateHaveSameHashCode() {
        var id = UUID.randomUUID();

        var publisher1 = buildPublisher(id);
        var publisher2 = buildPublisher(id)
                .setState("FL");

        Assertions.assertThat(publisher1)
                .hasSameHashCodeAs(publisher2);
    }

    @Test
    void publishersWithDifferentIdsAndTheSameStateAreNotEqual() {
        var publisher1 = buildPublisher(UUID.randomUUID());
        var publisher2 = buildPublisher(UUID.randomUUID());

        Assertions.assertThat(publisher1)
                .isNotEqualTo(publisher2);
    }

    @Test
    void publishersWithSameIdsButDifferentStateAreEqual() {
        var id = UUID.randomUUID();

        var publisher1 = buildPublisher(id);
        var publisher2 = buildPublisher(id)
                .setState("FL");

        Assertions.assertThat(publisher1)
                .isEqualTo(publisher2);
    }

    @NotNull
    private PublisherEntity buildPublisher(@NotNull UUID id) {
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
