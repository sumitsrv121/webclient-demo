package com.srv.sumit.webclient_demo.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srv.sumit.webclient_demo.util.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

@Component
public class NonBlockingWebClientHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(NonBlockingWebClientHelper.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public NonBlockingWebClientHelper(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    private URI buildUri(String baseUrl, String uriPath, Map<String, String> queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl).path(uriPath);
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach(builder::queryParam);
        }
        return builder.build().encode().toUri();
    }

    private Retry getRetryStrategy() {
        return Retry.backoff(3, Duration.ofSeconds(2))
                .jitter(0.75)
                .filter(throwable -> throwable instanceof WebClientResponseException)
                .doAfterRetry(retrySignal -> LOGGER.warn("Retrying request, attempt: {}", retrySignal.totalRetries()));
    }

    public <T> Mono<T> get(String baseUrl, String uriPath, Class<T> clazz, Map<String, String> headers, Map<String, String> queryParams) {
        URI uri = buildUri(baseUrl, uriPath, queryParams);
        return webClient.get()
                .uri(uri)
                .headers(httpHeaders -> {
                    if (headers != null) {
                        httpHeaders.setAll(headers);
                    }
                })
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> ResponseHandler.handleResponse(response, clazz, objectMapper))
                .retryWhen(getRetryStrategy())
                .doOnError(error -> LOGGER.error("GET request failed for URI {}: {}", uri, error.getMessage()))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    LOGGER.error("HTTP error ({}): {}", ex.getStatusCode(), ex.getMessage());
                    return Mono.error(new RuntimeException("HTTP error: " + ex.getStatusCode(), ex));
                });
    }

    public <T, R> Mono<T> post(String baseUrl, String uriPath, R body, Class<T> clazz, Map<String, String> headers, Map<String, String> queryParams) {
        URI uri = buildUri(baseUrl, uriPath, queryParams);
        return webClient.post()
                .uri(uri)
                .headers(httpHeaders -> {
                    if (headers != null) {
                        httpHeaders.setAll(headers);
                    }
                })
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> ResponseHandler.handleResponse(response, clazz, objectMapper))
                .retryWhen(getRetryStrategy())
                .doOnError(error -> LOGGER.error("POST request failed for URI {}: {}", uri, error.getMessage()))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    LOGGER.error("HTTP error ({}): {}", ex.getStatusCode(), ex.getMessage());
                    return Mono.error(new RuntimeException("HTTP error: " + ex.getStatusCode(), ex));
                });
    }

    public <T, R> Mono<T> put(String baseUrl, String uriPath, R body, Class<T> clazz, Map<String, String> headers, Map<String, String> queryParams) {
        URI uri = buildUri(baseUrl, uriPath, queryParams);
        return webClient.put()
                .uri(uri)
                .headers(httpHeaders -> {
                    if (headers != null) {
                        httpHeaders.setAll(headers);
                    }
                })
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> ResponseHandler.handleResponse(response, clazz, objectMapper))
                .retryWhen(getRetryStrategy())
                .doOnError(error -> LOGGER.error("PUT request failed for URI {}: {}", uri, error.getMessage()))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    LOGGER.error("HTTP error ({}): {}", ex.getStatusCode(), ex.getMessage());
                    return Mono.error(new RuntimeException("HTTP error: " + ex.getStatusCode(), ex));
                });
    }

    public Mono<Void> delete(String baseUrl, String uriPath, Map<String, String> headers, Map<String, String> queryParams) {
        URI uri = buildUri(baseUrl, uriPath, queryParams);
        return webClient.delete()
                .uri(uri)
                .headers(httpHeaders -> {
                    if (headers != null) {
                        httpHeaders.setAll(headers);
                    }
                })
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(getRetryStrategy())
                .doOnError(error -> LOGGER.error("DELETE request failed for URI {}: {}", uri, error.getMessage()))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    LOGGER.error("HTTP error ({}): {}", ex.getStatusCode(), ex.getMessage());
                    return Mono.error(new RuntimeException("HTTP error: " + ex.getStatusCode(), ex));
                });
    }
}
