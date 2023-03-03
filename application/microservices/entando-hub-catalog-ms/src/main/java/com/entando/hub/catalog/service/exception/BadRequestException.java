package com.entando.hub.catalog.service.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String errorMessage) {
        super(errorMessage);
    }

}
