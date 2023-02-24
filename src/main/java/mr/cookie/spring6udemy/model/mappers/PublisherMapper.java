package mr.cookie.spring6udemy.model.mappers;

import mr.cookie.spring6udemy.model.entities.PublisherDto;
import mr.cookie.spring6udemy.model.model.Publisher;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PublisherMapper {

    Publisher map(PublisherDto source);

    List<Publisher> mapToModel(Iterable<PublisherDto> source);

}
