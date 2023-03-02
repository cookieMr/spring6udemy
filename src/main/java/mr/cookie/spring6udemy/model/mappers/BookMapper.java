package mr.cookie.spring6udemy.model.mappers;

import mr.cookie.spring6udemy.model.entities.BookEntity;
import mr.cookie.spring6udemy.model.model.Book;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {

    @Nullable
    Book map(@Nullable BookEntity source);

    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    @Nullable
    BookEntity map(@Nullable Book source);

    @Nullable
    List<Book> mapToModel(@Nullable Iterable<BookEntity> source);

}
