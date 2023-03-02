package mr.cookie.spring6udemy.model.mappers;

import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import mr.cookie.spring6udemy.model.model.PublisherDto;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PublisherMapper {

    @Nullable
    PublisherDto map(@Nullable PublisherEntity source);

    @Mapping(target = "books", ignore = true)
    @Nullable
    PublisherEntity map(@Nullable PublisherDto source);

    @Nullable
    List<PublisherDto> mapToModel(@Nullable Iterable<PublisherEntity> source);

}
