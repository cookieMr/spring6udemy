package mr.cookie.spring6udemy.utils;

import java.util.List;
import mr.cookie.spring6udemy.model.dtos.BookDto;

public record MvcResponseWithBookContent(
        List<BookDto> content
) {
}
