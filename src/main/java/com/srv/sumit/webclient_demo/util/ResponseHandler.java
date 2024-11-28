package com.srv.sumit.webclient_demo.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;

public class ResponseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHandler.class);

    public static <T> Mono<T> handleResponse(String responseBody, Class<T> clazz, ObjectMapper objectMapper) {
        try {
            T parsedObject = objectMapper.readValue(responseBody, clazz);
            return Mono.just(parsedObject);
        } catch (Exception ex) {
            LOGGER.error("Failed to parse response: {}", ex.getMessage());
            return Mono.error(new RuntimeException("Response parsing error", ex));
        }
    }

    public static <T> Mono<List<T>> handleListResponse(String responseBody, Class<T> clazz, ObjectMapper objectMapper) {
        try {
            JavaType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            List<T> list = objectMapper.readValue(responseBody, collectionType);
            return Mono.just(list);
        } catch (Exception ex) {
            LOGGER.error("Failed to parse response to list: {}", ex.getMessage());
            return Mono.error(new RuntimeException("Response parsing error", ex));
        }
    }
}

