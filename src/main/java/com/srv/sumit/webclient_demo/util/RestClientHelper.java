package com.srv.sumit.webclient_demo.util;

import com.srv.sumit.webclient_demo.exception.HttpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class to handle HTTP requests using Spring RestClient.
 */
@Component
public class RestClientHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientHelper.class);

    private final RestClient restClient;

    public RestClientHelper(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    /**
     * Makes a POST request and parses the response to the specified class type.
     *
     * @param baseUrl       The base URL of the API.
     * @param uriPath       The endpoint path of the API.
     * @param headers       Custom headers for the request.
     * @param body          The request body object.
     * @param pathVariables Path variables to replace in the URI.
     * @param clazz         The class type of the expected response.
     * @param <T>           The type of the expected response.
     * @param <R>           The type of the request body.
     * @return The parsed response object.
     * @throws HttpClientException in case of an HTTP error or processing error.
     */
    public <T, R> T post(String baseUrl, String uriPath, Map<String, String> headers, R body,
                         Map<String, String> pathVariables, Class<T> clazz) {
        validateInputs(baseUrl, uriPath, clazz);

        URI fullUri = constructUri(baseUrl, uriPath, pathVariables);
        try {
            LOGGER.info("Making POST request to {}", fullUri);

            ResponseEntity<T> response = restClient
                    .method(HttpMethod.POST)
                    .uri(fullUri)
                    .headers(httpHeaders -> addHeaders(httpHeaders, headers))
                    .body(body)
                    .retrieve()
                    .toEntity(clazz);

            LOGGER.debug("POST request successful. Response: {}", response);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusEx) {
            // Handle HTTP errors with specific status codes
            LOGGER.error("Error during POST request to {}: Status code {} - {}",
                    fullUri, httpStatusEx.getStatusCode(), httpStatusEx.getMessage(), httpStatusEx);
            throw new HttpClientException("HTTP error during POST request",
                    httpStatusEx.getStatusCode().value(), httpStatusEx);

        } catch (Exception ex) {
            // Handle other unexpected errors
            LOGGER.error("Error during POST request to {}: {}", fullUri, ex.getMessage(), ex);
            throw new HttpClientException("Unexpected error during POST request", 500, ex);
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
    private URI constructUri(String baseUrl, String uriPath, Map<String, String> pathVariables) {
        String fullUri = baseUrl + uriPath;

        if (pathVariables != null && !pathVariables.isEmpty()) {
            for (Map.Entry<String, String> entry : pathVariables.entrySet()) {
                fullUri = fullUri.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return URI.create(fullUri);
    }

    /**
     * Validates inputs for the request.
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

    /**
     * Adds custom headers to the HTTP request.
     *
     * @param httpHeaders The HttpHeaders object to populate.
     * @param headers     A map of custom headers.
     */
    private void addHeaders(HttpHeaders httpHeaders, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(httpHeaders::add);
        }
    }


    /**
     * Deserialize the response body based on the class type provided.
     *
     * <p>This method checks if the class type is an array or a list and deserializes accordingly.
     * If the class type is an array, it will deserialize to a list of the array type.</p>
     *
     * @param responseBody The body of the response as a String.
     * @param clazz        The class type of the expected response.
     * @param <T>          The type of the expected response.
     * @return The deserialized object of the provided class type.
     * @throws Exception if there is a problem with deserialization.
     */
    /*private <T> T deserializeResponse(String responseBody, Class<T> clazz) throws Exception {
        if (clazz.isArray()) {
            // If it's an array (e.g., Map[].class), deserialize into List of that array type (List<Map>)
            return objectMapper.readValue(responseBody, new TypeReference<List<Object>>() {});
        } else {
            // Default case: deserialize to the provided class type
            return objectMapper.readValue(responseBody, clazz);
        }
    }*/

    /*
    private void addPathVariables(UriBuilder uriBuilder, Map<String, String> pathVariables) {
        if (pathVariables != null && !pathVariables.isEmpty()) {
            pathVariables.forEach(uriBuilder::queryParam);
        }
    }

  /*  /**
 * Replaces placeholders in the URI path with actual values.
 *
 * @param uriPath       The URI path containing placeholders (e.g., /users/{id}).
 * @param pathVariables A map of placeholder keys and their corresponding values.
 * @return The resolved URI path with placeholders replaced by actual values.
 */
   /* private String resolvePathVariables(String uriPath, Map<String, String> pathVariables) {
        if (pathVariables == null || pathVariables.isEmpty()) {
            return uriPath;
        }
        for (Map.Entry<String, String> entry : pathVariables.entrySet()) {
            uriPath = uriPath.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return uriPath;
    }

     String resolvedPath = resolvePathVariables(uriPath, pathVariables)


     */
}
