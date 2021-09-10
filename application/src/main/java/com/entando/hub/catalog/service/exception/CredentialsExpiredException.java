package com.entando.hub.catalog.service.exception;

public class CredentialsExpiredException extends OidcException {

    public CredentialsExpiredException(final Throwable throwable) {
        super(throwable);
    }

}
