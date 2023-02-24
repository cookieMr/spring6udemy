package mr.cookie.spring6udemy.repositories;

import mr.cookie.spring6udemy.model.entities.PublisherDto;
import org.springframework.data.repository.CrudRepository;

public interface PublisherRepository extends CrudRepository<PublisherDto, Long> {

}
