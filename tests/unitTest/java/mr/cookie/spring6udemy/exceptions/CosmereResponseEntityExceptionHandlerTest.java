package mr.cookie.spring6udemy.exceptions;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.stream.Stream;

class CosmereResponseEntityExceptionHandlerTest {

    private final CosmereResponseEntityExceptionHandler exceptionHandler = new CosmereResponseEntityExceptionHandler();

    @ParameterizedTest
    @MethodSource
    void shouldHandleEntityNotFoundException(EntityNotFoundException exception) {
        var result = exceptionHandler.handleEntityNotFoundException(exception);

        assertThat(result)
                .isNotNull()
                .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
                .extracting(HttpEntity::getBody)
                .returns(exception.getMessage(), ErrorDto::message);
    }

    static Stream<EntityNotFoundException> shouldHandleEntityNotFoundException() {
        return Stream.of(
                EntityNotFoundException.ofAuthor(randomUUID()),
                EntityNotFoundException.ofBook(randomUUID()),
                EntityNotFoundException.ofPublisher(randomUUID()));
    }

    @ParameterizedTest
    @MethodSource
    void shouldHandleEntityExistsException(EntityExistsException exception) {
        var result = exceptionHandler.handleEntityExistsException(exception);

        assertThat(result)
                .isNotNull()
                .returns(HttpStatus.CONFLICT, ResponseEntity::getStatusCode)
                .extracting(HttpEntity::getBody)
                .returns(exception.getMessage(), ErrorDto::message);
    }

    static Stream<EntityExistsException> shouldHandleEntityExistsException() {
        return Stream.of(
                EntityExistsException.ofAuthor(),
                EntityExistsException.ofBook(),
                EntityExistsException.ofPublisher());
    }

}
