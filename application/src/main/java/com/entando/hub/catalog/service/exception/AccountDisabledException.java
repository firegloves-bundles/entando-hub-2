package com.entando.hub.catalog.service.exception;

public class AccountDisabledException extends OidcException {

    public AccountDisabledException(final Throwable throwable) {
        super(throwable);
    }
}
