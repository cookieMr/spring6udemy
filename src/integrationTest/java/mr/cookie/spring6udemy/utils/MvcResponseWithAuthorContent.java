package mr.cookie.spring6udemy.utils;

import mr.cookie.spring6udemy.model.dtos.AuthorDto;

import java.util.List;

public record MvcResponseWithAuthorContent(
        List<AuthorDto> content
) {
}
