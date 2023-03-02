package mr.cookie.spring6udemy.model.mappers;

import mr.cookie.spring6udemy.model.entities.PublisherEntity;
import mr.cookie.spring6udemy.model.model.Publisher;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PublisherMapper {

    @Nullable
    Publisher map(@Nullable PublisherEntity source);

    @Mapping(target = "books", ignore = true)
    @Nullable
    PublisherEntity map(@Nullable Publisher source);

    @Nullable
    List<Publisher> mapToModel(@Nullable Iterable<PublisherEntity> source);

}
