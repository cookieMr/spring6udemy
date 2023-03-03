package mr.cookie.spring6udemy.repositories;

import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PublisherRepository extends CrudRepository<PublisherEntity, UUID> {

}
