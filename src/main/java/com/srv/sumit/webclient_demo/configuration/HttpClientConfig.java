package com.srv.sumit.webclient_demo.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

    /**
     * Configures and provides a CloseableHttpClient with connection pooling and timeouts.
     *
     * @return a fully configured CloseableHttpClient.
     */
    @Bean
    public CloseableHttpClient closeableHttpClient() {
        // Connection pooling manager configuration
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

        connectionManager.setMaxTotal(200); // Maximum total connections
        connectionManager.setDefaultMaxPerRoute(50); // Maximum connections per route

        // Build the default request configuration
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(5)) // Timeout for acquiring a connection
                .setConnectTimeout(Timeout.ofSeconds(10)) // Timeout for establishing a connection
                .setResponseTimeout(Timeout.ofSeconds(15)) // Timeout for receiving a response
                .build();

        // Build the CloseableHttpClient
        /*HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(defaultRequestConfig)
                .evictExpiredConnections() // Automatically evict expired connections
                .evictIdleConnections(TimeValue.ofMinutes(1)) // Evict idle connections after 1 minute
                .disableAutomaticRetries();// Disable automatic retries to avoid unwanted replays*/

        HttpRequestRetryStrategy retryStrategy = new DefaultHttpRequestRetryStrategy(3, TimeValue.ofSeconds(1));

        // Build the CloseableHttpClient
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(defaultRequestConfig)
                .evictExpiredConnections() // Automatically evict expired connections
                .evictIdleConnections(TimeValue.ofMinutes(1)) // Evict idle connections after 1 minute
                //.disableAutomaticRetries() // Disable automatic retries to avoid unwanted replays
                .setRetryStrategy(retryStrategy)
                .build();
    }

    /**
     * Configures and provides an ObjectMapper for JSON serialization and deserialization.
     *
     * @return a fully configured ObjectMapper.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new ParameterNamesModule()) // Handles parameter names
                .registerModule(new Jdk8Module())           // Handles Java 8 optional and streams
                .registerModule(new JavaTimeModule())       // Handles Java 8 date/time APIs
                .findAndRegisterModules();                  // Automatically registers other compatible modules
    }
}
