package mr.cookie.spring6udemy.model.mappers;

import mr.cookie.spring6udemy.model.entities.AuthorDto;
import mr.cookie.spring6udemy.model.model.Author;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {

    @Nullable
    Author map(@Nullable AuthorDto source);

    @Mapping(target = "books", ignore = true)
    @Nullable
    AuthorDto map(@Nullable Author source);

    @Nullable
    List<Author> mapToModel(@Nullable Iterable<AuthorDto> source);

}
