package mr.cookie.spring6udemy.assertions;

import org.assertj.core.api.AbstractAssert;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnusedReturnValue")
public final class ResponseEntityAssertions<T> extends AbstractAssert<ResponseEntityAssertions<T>, ResponseEntity<T>> {

    private ResponseEntityAssertions(@Nullable ResponseEntity<T> responseEntity) {
        super(responseEntity, ResponseEntityAssertions.class);
    }

    public static <T> ResponseEntityAssertions<T> assertThat(@Nullable ResponseEntity<T> actual) {
        return new ResponseEntityAssertions<>(actual);
    }

    public ResponseEntityAssertions<T> hasStatus(@NotNull HttpStatusCode statusCode) {
        var actualStatusCode = actual.getStatusCode();
        if (!Objects.equals(actualStatusCode, statusCode)) {
            failWithMessage(
                    "Expected status code to be <%s> but was <%s>!",
                    statusCode.value(),
                    actualStatusCode.value());
        }

        return this;
    }

    public ResponseEntityAssertions<T> hasStatusOk() {
        return this.hasStatus(HttpStatus.OK);
    }

    public ResponseEntityAssertions<T> hasContentTypeAsApplicationJson() {
        var header = this.hasHeaderWithName(HttpHeaders.CONTENT_TYPE);

        if (!header.contains(MediaType.APPLICATION_JSON_VALUE)) {
            failWithMessage("Actual Content Type is not an <%s>!", MediaType.APPLICATION_JSON_VALUE);
        }

        return this;
    }

    private List<String> hasHeaderWithName(@NotNull String headerName) {
        if (CollectionUtils.isEmpty(actual.getHeaders())) {
            failWithMessage("Actual object has no headers!");
        }

        var header = actual.getHeaders().get(headerName);
        if (header == null) {
            failWithMessage("Expected header with name <%s> could not be found!", headerName);
        }

        return header;
    }

    public ResponseEntityAssertions<T> doesNotHaveHeader(@NotNull String headerName) {
        if (CollectionUtils.isEmpty(actual.getHeaders())) {
            return this;
        }

        var header = actual.getHeaders().get(headerName);
        if (header == null) {
            return this;
        }

        failWithMessage("Header with name <%s> was found, but expected not to!", headerName);
        return this;
    }

}
