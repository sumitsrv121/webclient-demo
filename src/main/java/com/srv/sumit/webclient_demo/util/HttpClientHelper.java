package com.srv.sumit.webclient_demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srv.sumit.webclient_demo.exception.HttpClientException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class to handle HTTP requests using Apache HttpClient.
 */
@Component
public class HttpClientHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientHelper.class);

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpClientHelper(CloseableHttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Makes a POST request and parses the response to the specified class type.
     *
     * @param baseUrl      The base URL of the API.
     * @param uriPath      The endpoint path of the API.
     * @param headers      Custom headers for the request.
     * @param body         The request body object.
     * @param pathVariables Path variables to replace in the URI.
     * @param clazz        The class type of the expected response.
     * @param <T>          The type of the expected response.
     * @param <R>          The type of the request body.
     * @return The parsed response object.
     * @throws HttpClientException in case of an HTTP error or processing error.
     */
    public <T, R> T post(String baseUrl, String uriPath, Map<String, String> headers, R body,
                         Map<String, String> pathVariables, Class<T> clazz) {
        validateInputs(baseUrl, uriPath, clazz);

        String fullUrl = constructUri(baseUrl, uriPath, pathVariables);
        HttpPost httpPost = new HttpPost(fullUrl);

        try {
            // Add headers
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(httpPost::addHeader);
            }

            // Set request body
            if (body != null) {
                String jsonBody = objectMapper.writeValueAsString(body);
                httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
            }

            // Define a response handler to process the response
            HttpClientResponseHandler<T> responseHandler = response -> processResponse(response, fullUrl, clazz);

            // Execute the request using the response handler
            return httpClient.execute(httpPost, responseHandler);

        } catch (IOException ex) {
            LOGGER.error("I/O error during POST request to {}: {}", fullUrl, ex.getMessage(), ex);
            throw new HttpClientException("I/O error during POST request", 500, ex);
        }
    }

    /**
     * Constructs the final URI by replacing path variables.
     *
     * @param baseUrl       The base URL (including scheme, e.g., <a href="http://example.com">...</a>).
     * @param uriPath       The URI path with placeholders for path variables.
     * @param pathVariables A map of path variables to replace placeholders in the URI.
     * @return The constructed URI with path variables substituted.
     */
    private String constructUri(String baseUrl, String uriPath, Map<String, String> pathVariables) {
        String fullUri = baseUrl + uriPath;

        if (pathVariables != null && !pathVariables.isEmpty()) {
            for (Map.Entry<String, String> entry : pathVariables.entrySet()) {
                fullUri = fullUri.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return fullUri;
    }

    /**
     * Processes the HTTP response and maps it to the specified class type.
     *
     * @param response The HTTP response object.
     * @param url      The requested URL.
     * @param clazz    The class type to map the response to.
     * @param <T>      The type of the response.
     * @return The mapped response object.
     * @throws IOException if an error occurs during processing.
     */
    private <T> T processResponse(ClassicHttpResponse response, String url, Class<T> clazz) throws IOException {
        int statusCode = response.getCode();
        HttpEntity entity = response.getEntity();

        if (statusCode >= 200 && statusCode < 300) {
            // Success response
            if (entity != null) {
                String responseBody = new String(entity.getContent().readAllBytes(), StandardCharsets.UTF_8);
                LOGGER.debug("Successful response from {}: {}", url, responseBody);
                return objectMapper.readValue(responseBody, clazz);
            } else {
                throw new HttpClientException("Empty response body", statusCode, null);
            }
        } else {
            // Error response
            String errorMessage = entity != null
                    ? new String(entity.getContent().readAllBytes(), StandardCharsets.UTF_8)
                    : "No response body";
            LOGGER.error("HTTP error from {}: {} - {}", url, statusCode, errorMessage);
            throw new HttpClientException(errorMessage, statusCode, null);
        }
    }

    /**
     * Validates inputs for the POST request.
     *
     * @param baseUrl The base URL of the API.
     * @param uriPath The endpoint path of the API.
     * @param clazz   The class type of the expected response.
     * @param <T>     The type of the response.
     * @throws IllegalArgumentException if any input is invalid.
     */
    private <T> void validateInputs(String baseUrl, String uriPath, Class<T> clazz) {
        if (Objects.isNull(baseUrl) || baseUrl.isBlank()) {
            throw new IllegalArgumentException("Base URL must not be null or empty");
        }
        if (Objects.isNull(uriPath) || uriPath.isBlank()) {
            throw new IllegalArgumentException("URI path must not be null or empty");
        }
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("Response class type must not be null");
        }
    }
}
