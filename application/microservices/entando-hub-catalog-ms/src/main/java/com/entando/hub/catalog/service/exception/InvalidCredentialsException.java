package com.entando.hub.catalog.service.exception;

public class InvalidCredentialsException extends OidcException {

    public InvalidCredentialsException(final Throwable throwable) {
        super(throwable);
    }
}
