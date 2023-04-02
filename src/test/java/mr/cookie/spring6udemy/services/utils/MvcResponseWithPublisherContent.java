package mr.cookie.spring6udemy.services.utils;

import mr.cookie.spring6udemy.model.dtos.PublisherDto;

import java.util.List;

public record MvcResponseWithPublisherContent(
        List<PublisherDto> content
) {
}
