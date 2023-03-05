package mr.cookie.spring6udemy.units.repositories;

import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import mr.cookie.spring6udemy.repositories.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void saveAuthor() {
        var author = AuthorEntity.builder()
                .firstName("Brandon")
                .lastName("Sanderson")
                .build();

        var result = this.authorRepository.save(author);

        assertThat(result)
                .isNotNull()
                .isSameAs(author);
        assertThat(result.getId())
                .isNotNull();
    }

}
