package cde.chameleon.api.actor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.truth.Truth;
import lombok.Getter;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Getter
public abstract class ApiActor extends AuthenticationActor {
    public static final Duration HTTP_REQUEST_TIMEOUT = Duration.ofSeconds(5);

    private final String mappingPrefix;

    private HttpResponse<String> httpResponse;

    protected ApiActor(String mappingPrefix) {
        this.mappingPrefix = mappingPrefix;
    }

    protected void sendRequest(HttpRequest httpRequest) throws IOException, InterruptedException {
        httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpRequest.Builder newHttpRequest(String mappingPath) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4444/" + mappingPrefix + mappingPath))
                .setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getToken())
                .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .timeout(HTTP_REQUEST_TIMEOUT);
    }

    public HttpStatus getHttpResponseStatus() {
        Truth.assertThat(httpResponse).isNotNull();
        return HttpStatus.valueOf(httpResponse.statusCode());
    }

    public <T> T getHttpResponseBodyAs(Class<T> type) throws JsonProcessingException {
        Truth.assertThat(httpResponse).isNotNull();
        return new ObjectMapper().readValue(httpResponse.body(), type);
    }

    public <T> T getHttpResponseBodyAs(TypeReference<T> typeReference) throws JsonProcessingException {
        Truth.assertThat(httpResponse).isNotNull();
        return new ObjectMapper().readValue(httpResponse.body(), typeReference);
    }
}
