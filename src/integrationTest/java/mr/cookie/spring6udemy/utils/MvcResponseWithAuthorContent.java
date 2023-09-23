package mr.cookie.spring6udemy.utils;

import java.util.List;
import mr.cookie.spring6udemy.model.dtos.AuthorDto;

public record MvcResponseWithAuthorContent(
        List<AuthorDto> content
) {
}
