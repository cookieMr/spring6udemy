package mr.cookie.spring6udemy.repositories;

import mr.cookie.spring6udemy.domain.Publisher;
import org.springframework.data.repository.CrudRepository;

public interface PublisherRepository extends CrudRepository<Publisher, Long> {

}
