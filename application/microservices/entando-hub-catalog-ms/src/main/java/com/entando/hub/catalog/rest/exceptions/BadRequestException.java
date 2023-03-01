package com.entando.hub.catalog.rest.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String errorMessage) {
        super(errorMessage);
    }

}
