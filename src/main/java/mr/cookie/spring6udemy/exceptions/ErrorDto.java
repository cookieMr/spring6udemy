package mr.cookie.spring6udemy.exceptions;

import lombok.Builder;

@Builder
public record ErrorDto(
        String message
) {
}
