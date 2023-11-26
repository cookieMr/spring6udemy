package mr.cookie.spring6udemy.rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpEntityUtils {

    public static HttpEntity<?> createRequestWithHeaders() {
        return createRequestWithHeaders(null);
    }

    public static <T> HttpEntity<T> createRequestWithHeaders(T body) {
        var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        if (body != null) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }

        return new HttpEntity<>(body, headers);
    }

}
