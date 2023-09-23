package mr.cookie.spring6udemy.utils;

import mr.cookie.spring6udemy.model.dtos.BookDto;

import java.util.List;

public record MvcResponseWithBookContent(
        List<BookDto> content
) {
}
