package com.srv.sumit.webclient_demo.configuration;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {
    @Bean
    @Primary
    public RestClient.Builder restClientBuilder() {
        // Create a pooling connection manager to efficiently manage connections
        PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
        poolingConnManager.setMaxTotal(200); // Max connections for the entire pool
        poolingConnManager.setDefaultMaxPerRoute(20); // Max connections per route

        // Define connection and socket timeouts
        RequestConfig requestConfig = RequestConfig.custom()
                //.setConnectTimeout(Timeout.ofMilliseconds(5000))  // 5 seconds connect timeout
                .setResponseTimeout(Timeout.ofMilliseconds(10000)) // 10 seconds read timeout
                .build();

        // Create HttpClient with pooling connection manager and timeouts
        var httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig) // Apply the timeouts
                .setConnectionManager(poolingConnManager) // Set connection manager for pooling
                .build();

        // Use HttpComponentsClientHttpRequestFactory to bridge HttpClient to RestClient
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        // Return RestClient.builder() with the custom ClientHttpFactory (HttpClient)
        return RestClient.builder()
                .requestFactory(factory)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json");  // Set the custom ClientHttpFactory
    }


}
