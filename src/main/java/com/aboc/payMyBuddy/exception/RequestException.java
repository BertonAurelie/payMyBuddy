package com.aboc.payMyBuddy.exception;

public class RequestException extends RuntimeException {
    public RequestException(String errorMessage) {
        super(errorMessage);
    }
}
