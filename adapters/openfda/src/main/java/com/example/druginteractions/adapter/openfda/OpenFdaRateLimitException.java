package com.example.druginteractions.adapter.openfda;

public class OpenFdaRateLimitException extends OpenFdaException {
    public OpenFdaRateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
