package com.srv.sumit.webclient_demo.exception;

public class HttpClientException extends RuntimeException {

    private final int statusCode;

    public HttpClientException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

