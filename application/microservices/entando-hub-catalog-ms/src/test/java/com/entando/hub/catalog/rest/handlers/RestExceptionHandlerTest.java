package com.entando.hub.catalog.rest.handlers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.entando.hub.catalog.service.exception.BadRequestException;
import com.entando.hub.catalog.service.exception.ConflictException;
import com.entando.hub.catalog.service.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

public class RestExceptionHandlerTest {

    private static final String ERROR_MSG = "Error message";

    @Test
    public void badRequestErrorTest() {
        RestExceptionHandler restExceptionHandler = new RestExceptionHandler();
        ResponseEntity<ErrorResponse> error = restExceptionHandler.customHandleRequest(
                new BadRequestException(ERROR_MSG));
        assertThat(error.getBody(), instanceOf(ErrorResponse.class));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        assertEquals(ERROR_MSG, error.getBody().getError());
    }

    @Test
    public void notFoundErrorTest() {
        RestExceptionHandler restExceptionHandler = new RestExceptionHandler();
        ResponseEntity<ErrorResponse> error = restExceptionHandler.customHandleRequest(
                new NotFoundException(ERROR_MSG));
        assertThat(error.getBody(), instanceOf(ErrorResponse.class));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        assertEquals(ERROR_MSG, error.getBody().getError());
    }

    @Test
    public void accessDeniedErrorTest() {
        RestExceptionHandler restExceptionHandler = new RestExceptionHandler();
        ResponseEntity<ErrorResponse> error = restExceptionHandler.customHandleRequest(
                new AccessDeniedException(ERROR_MSG));
        assertThat(error.getBody(), instanceOf(ErrorResponse.class));
        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        assertEquals(ERROR_MSG, error.getBody().getError());
    }

    @Test
    public void illegalArgumentErrorTest() {
        RestExceptionHandler restExceptionHandler = new RestExceptionHandler();
        ResponseEntity<ErrorResponse> error = restExceptionHandler.customHandleRequest(
                new IllegalArgumentException(ERROR_MSG));
        assertThat(error.getBody(), instanceOf(ErrorResponse.class));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        assertEquals(ERROR_MSG, error.getBody().getError());
    }
    @Test
    public void conflictErrorTest() {
        RestExceptionHandler restExceptionHandler = new RestExceptionHandler();
        ResponseEntity<ErrorResponse> error = restExceptionHandler.customHandleRequest(
                new ConflictException(ERROR_MSG));
        assertThat(error.getBody(), instanceOf(ErrorResponse.class));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        assertEquals(ERROR_MSG, error.getBody().getError());
    }
}
