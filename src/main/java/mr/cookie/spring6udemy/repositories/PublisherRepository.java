package mr.cookie.spring6udemy.repositories;

import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PublisherRepository extends JpaRepository<PublisherEntity, UUID> {

    Optional<PublisherEntity> findByName(String name);

}
