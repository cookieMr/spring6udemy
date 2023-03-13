package mr.cookie.spring6udemy.units.repositories;

import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import mr.cookie.spring6udemy.repositories.PublisherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class PublisherRepositoryTest {

    private static final Supplier<PublisherEntity> PUBLISHER_SUPPLIER = () -> PublisherEntity.builder()
            .name("DragonSteel Books")
            .address("PO Box 698")
            .city("American Fork")
            .state("UT")
            .zipCode("84003")
            .build();

    @Autowired
    private PublisherRepository publisherRepository;

    @Test
    void savePublisher() {
        var publisher = PUBLISHER_SUPPLIER.get();

        var result = this.publisherRepository.save(publisher);

        assertThat(result)
                .isNotNull()
                .isSameAs(publisher);
        assertThat(result.getId())
                .isNotNull();
    }

    static Stream<Consumer<PublisherEntity>> consumerWithExpectedErrorMessage() {
        return Stream.of(
                publisher -> publisher.setName(null),
                publisher -> publisher.setAddress(null),
                publisher -> publisher.setCity(null),
                publisher -> publisher.setState(null),
                publisher -> publisher.setZipCode(null)
        );
    }

    @ParameterizedTest
    @MethodSource("consumerWithExpectedErrorMessage")
    void savePublisherShouldFailValidation(Consumer<PublisherEntity> publisherModifier) {
        var publisher = PUBLISHER_SUPPLIER.get();
        publisherModifier.accept(publisher);

        assertThatThrownBy(() -> {
            this.publisherRepository.save(publisher);
            this.publisherRepository.flush();
        })
                .isNotNull()
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("could not execute statement; SQL [n/a]; constraint [null]");
    }


}
