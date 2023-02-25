package mr.cookie.spring6udemy.model.mappers;

import mr.cookie.spring6udemy.model.entities.BookDto;
import mr.cookie.spring6udemy.model.model.Book;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {

    @Nullable
    Book map(@Nullable BookDto source);

    @Nullable
    List<Book> mapToModel(@Nullable Iterable<BookDto> source);

}
