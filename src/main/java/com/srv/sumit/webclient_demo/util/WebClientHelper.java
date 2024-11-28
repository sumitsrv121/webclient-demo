package com.srv.sumit.webclient_demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class WebClientHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebClientHelper.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public WebClientHelper(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        objectMapper = objectMapper.registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        this.objectMapper = objectMapper;

    }

    /**
     * Blocking GET request to retrieve a single entity.
     *
     * @param baseUrl      Base URL of the API.
     * @param uriPath      Path to append to the base URL.
     * @param clazz        The class type to map the response to.
     * @param headers      Any headers to send with the request.
     * @param pathVariables Path variables to be replaced in the URI.
     * @param queryParams  Query parameters to append to the URI.
     * @param <T>          The response type.
     * @return The parsed response.
     */
    public <T> T get(String baseUrl, String uriPath, Class<T> clazz, Map<String, String> headers,
                     Map<String, String> pathVariables, MultiValueMap<String, String> queryParams) {
        try {
            URI finalUri = constructUri(baseUrl, uriPath, pathVariables, queryParams);
            String response = webClient.get()
                    .uri(finalUri)
                    .headers(httpHeaders -> httpHeaders.setAll(getOrDefaultHeaders(headers))) // Set custom headers
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> Mono.error(new RuntimeException("HTTP error: " + clientResponse.statusCode())))
                    .bodyToMono(String.class) // Get the response body as a String
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)).jitter(0.75)) // Retry logic
                    .block(); // Block to wait for the response

            // Handle and parse the response using the ResponseHandler
            return ResponseHandler.handleResponse(response, clazz, objectMapper).block();
        } catch (WebClientResponseException ex) {
            LOGGER.error("Error while making GET request: {}", ex.getMessage());
            throw new RuntimeException("Error while making GET request", ex);
        } catch (URISyntaxException ex) {
            LOGGER.error("Error in URI syntax: {}", ex.getMessage());
            throw new RuntimeException("Error in URI syntax", ex);
        }
    }

    /**
     * Blocking GET request to retrieve a list of entities.
     *
     * @param baseUrl      Base URL of the API.
     * @param uriPath      Path to append to the base URL.
     * @param clazz        The class type to map the response to.
     * @param headers      Any headers to send with the request.
     * @param pathVariables Path variables to be replaced in the URI.
     * @param queryParams  Query parameters to append to the URI.
     * @param <T>          The response type.
     * @return A list of parsed responses.
     */
    public <T> List<T> getList(String baseUrl, String uriPath, Class<T> clazz, Map<String, String> headers,
                               Map<String, String> pathVariables, MultiValueMap<String, String> queryParams) {
        try {
            URI finalUri = constructUri(baseUrl, uriPath, pathVariables, queryParams);
            String response = webClient.get()
                    .uri(finalUri)
                    .headers(httpHeaders -> httpHeaders.setAll(getOrDefaultHeaders(headers))) // Set custom headers
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> Mono.error(new RuntimeException("HTTP error: " + clientResponse.statusCode())))
                    .bodyToMono(String.class) // Get the response body as a String
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)).jitter(0.75)) // Retry logic
                    .block(); // Block to wait for the response

            // Handle and parse the response using the ResponseHandler
            return ResponseHandler.handleListResponse(response, clazz, objectMapper).block();
        } catch (WebClientResponseException ex) {
            LOGGER.error("Error while making GET request for list: {}", ex.getMessage());
            throw new RuntimeException("Error while making GET request for list", ex);
        } catch (URISyntaxException ex) {
            LOGGER.error("Error in URI syntax: {}", ex.getMessage());
            throw new RuntimeException("Error in URI syntax", ex);
        }
    }

    /**
     * Blocking POST request to send data and receive a response.
     *
     * @param baseUrl      Base URL of the API.
     * @param uriPath      Path to append to the base URL.
     * @param clazz        The class type to map the response to.
     * @param headers      Any headers to send with the request.
     * @param pathVariables Path variables to be replaced in the URI.
     * @param body         The request body.
     * @param <T>          The response type.
     * @param <R>          The request body type.
     * @return The parsed response.
     */
    public <T, R> T post(String baseUrl, String uriPath, Class<T> clazz, Map<String, String> headers,
                         Map<String, String> pathVariables, R body) {
        try {
            URI finalUri = constructUri(baseUrl, uriPath, pathVariables, null); // No query params for POST

            String response = webClient.post()
                    .uri(finalUri)
                    .headers(httpHeaders -> httpHeaders.setAll(getOrDefaultHeaders(headers))) // Set custom headers
                    .bodyValue(body) // Set the request body
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> Mono.error(new RuntimeException("HTTP error: " + clientResponse.statusCode())))
                    .bodyToMono(String.class) // Get the response body as a String
                    .block(); // Block to wait for the response

            // Handle and parse the response using the ResponseHandler
            return ResponseHandler.handleResponse(response, clazz, objectMapper).block();
        } catch (WebClientResponseException ex) {
            LOGGER.error("Error while making POST request: {}", ex.getMessage());
            throw new RuntimeException("Error while making POST request", ex);
        } catch (URISyntaxException ex) {
            LOGGER.error("Error in URI syntax: {}", ex.getMessage());
            throw new RuntimeException("Error in URI syntax", ex);
        }
    }

    /**
     * Blocking PUT request to update data and receive a response.
     *
     * @param baseUrl      Base URL of the API.
     * @param uriPath      Path to append to the base URL.
     * @param clazz        The class type to map the response to.
     * @param headers      Any headers to send with the request.
     * @param pathVariables Path variables to be replaced in the URI.
     * @param body         The request body.
     * @param <T>          The response type.
     * @param <R>          The request body type.
     * @return The parsed response.
     */
    public <T, R> T put(String baseUrl, String uriPath, Class<T> clazz, Map<String, String> headers,
                        Map<String, String> pathVariables, R body) {
        try {
            URI finalUri = constructUri(baseUrl, uriPath, pathVariables, null);
            String response = webClient.put()
                    .uri(finalUri)
                    .headers(httpHeaders -> httpHeaders.setAll(getOrDefaultHeaders(headers))) // Set custom headers
                    .bodyValue(body) // Set the request body
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> Mono.error(new RuntimeException("HTTP error: " + clientResponse.statusCode())))
                    .bodyToMono(String.class) // Get the response body as a String
                    .block(); // Block to wait for the response

            // Handle and parse the response using the ResponseHandler
            return ResponseHandler.handleResponse(response, clazz, objectMapper).block();
        } catch (WebClientResponseException ex) {
            LOGGER.error("Error while making PUT request: {}", ex.getMessage());
            throw new RuntimeException("Error while making PUT request", ex);
        } catch (URISyntaxException ex) {
            LOGGER.error("Error in URI syntax: {}", ex.getMessage());
            throw new RuntimeException("Error in URI syntax", ex);
        }
    }

    /**
     * Blocking DELETE request to delete an entity and receive a response.
     *
     * @param baseUrl      Base URL of the API.
     * @param uriPath      Path to append to the base URL.
     * @param headers      Any headers to send with the request.
     * @param pathVariables Path variables to be replaced in the URI.
     */
    public void delete(String baseUrl, String uriPath, Map<String, String> headers, Map<String, String> pathVariables) {
        try {
            URI finalUri = constructUri(baseUrl, uriPath, pathVariables, null);
            webClient.delete()
                    .uri(finalUri)
                    .headers(httpHeaders -> httpHeaders.setAll(getOrDefaultHeaders(headers))) // Set custom headers
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> Mono.error(new RuntimeException("HTTP error: " + clientResponse.statusCode())))
                    .bodyToMono(Void.class) // No body in DELETE request
                    .block(); // Block to wait for the response

        } catch (WebClientResponseException ex) {
            LOGGER.error("Error while making DELETE request: {}", ex.getMessage());
            throw new RuntimeException("Error while making DELETE request", ex);
        } catch (URISyntaxException ex) {
            LOGGER.error("Error in URI syntax: {}", ex.getMessage());
            throw new RuntimeException("Error in URI syntax", ex);
        }
    }


    /**
     * Constructs the final URI by combining the base URL, URI path, path variables, and query parameters.
     *
     * @param baseUrl       The base URL (including scheme, e.g., http://example.com).
     * @param uriPath       The URI path to be appended to the base URL.
     * @param pathVariables A map of path variables to replace placeholders in the URI.
     * @param queryParams   A map of query parameters to append to the URI.
     * @return The constructed URI.
     * @throws URISyntaxException if the constructed URI is invalid.
     */
    public URI constructUri(String baseUrl, String uriPath, Map<String, String> pathVariables,
                            MultiValueMap<String, String> queryParams) throws URISyntaxException {
        StringBuilder uriBuilder = buildUriPath(baseUrl, uriPath, pathVariables);
        if (queryParams != null && !queryParams.isEmpty()) {
            uriBuilder.append("?");
            queryParams.forEach((key, values) -> {
                values.forEach(value -> {
                    uriBuilder.append(key).append("=").append(value).append("&");
                });
            });
            // Remove the trailing "&"
            uriBuilder.deleteCharAt(uriBuilder.length() - 1);
        }

        return new URI(uriBuilder.toString());
    }

    /**
     * Builds the path part of the URI by appending the base URL and replacing path variables.
     *
     * @param baseUrl       The base URL (including scheme, e.g., http://example.com).
     * @param uriPath       The URI path to be appended to the base URL.
     * @param pathVariables A map of path variables to replace placeholders in the URI.
     * @return A StringBuilder containing the constructed URI path.
     * @throws IllegalArgumentException if the base URL doesn't contain a valid scheme (http:// or https://).
     */
    private static StringBuilder buildUriPath(String baseUrl, String uriPath, Map<String, String> pathVariables) {
        // Validate base URL
        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            throw new IllegalArgumentException("Base URL must include the scheme (http:// or https://).");
        }

        String fullUri = baseUrl + uriPath;

        // Replace path variables in URI path
        if (Objects.nonNull(pathVariables)) {
            for (Map.Entry<String, String> entry : pathVariables.entrySet()) {
                fullUri = fullUri.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        return new StringBuilder(fullUri);
    }

    /**
     * Returns a map of headers, ensuring that default headers are provided if none are supplied.
     *
     * @param headers A map of headers to be used in the request.
     * @return The provided headers, or an empty map if null.
     */
    public Map<String, String> getOrDefaultHeaders(Map<String, String> headers) {
        return Objects.nonNull(headers) ? headers : Map.of();
    }
}
