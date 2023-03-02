package mr.cookie.spring6udemy.model.mappers;

import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {

    @Nullable
    AuthorDto map(@Nullable AuthorEntity source);

    @Mapping(target = "books", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Nullable
    AuthorEntity map(@Nullable AuthorDto source);

    @Nullable
    List<AuthorDto> mapToModel(@Nullable Iterable<AuthorEntity> source);

}
