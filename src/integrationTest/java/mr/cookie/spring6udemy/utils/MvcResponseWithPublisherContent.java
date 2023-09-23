package mr.cookie.spring6udemy.utils;

import java.util.List;
import mr.cookie.spring6udemy.model.dtos.PublisherDto;

public record MvcResponseWithPublisherContent(
        List<PublisherDto> content
) {
}
