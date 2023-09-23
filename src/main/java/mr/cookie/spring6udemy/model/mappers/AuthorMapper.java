package mr.cookie.spring6udemy.model.mappers;

import java.util.List;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;
import mr.cookie.spring6udemy.model.entities.AuthorEntity;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {

    @Nullable
    AuthorDto map(@Nullable AuthorEntity source);

    @Mapping(target = "books", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Nullable
    AuthorEntity map(@Nullable AuthorDto source);

    @Nullable
    List<AuthorDto> mapToModel(@Nullable Iterable<AuthorEntity> source);

}
