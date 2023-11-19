package mr.cookie.spring6udemy.repositories;

import java.util.Optional;
import java.util.UUID;
import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<PublisherEntity, UUID> {

    Optional<PublisherEntity> findByName(String name);

}
