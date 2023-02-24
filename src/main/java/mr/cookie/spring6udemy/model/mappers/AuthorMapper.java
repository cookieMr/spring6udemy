package mr.cookie.spring6udemy.model.mappers;

import mr.cookie.spring6udemy.model.entities.AuthorDto;
import mr.cookie.spring6udemy.model.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {

    Author map(AuthorDto source);

    List<Author> mapToModel(Iterable<AuthorDto> source);

}
