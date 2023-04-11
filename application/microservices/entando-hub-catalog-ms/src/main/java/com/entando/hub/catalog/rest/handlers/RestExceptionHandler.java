package com.entando.hub.catalog.rest.handlers;

import com.entando.hub.catalog.service.exception.BadRequestException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorResponse> customHandleRequest(Exception ex) {
        ResponseEntity<ErrorResponse> responseEntity = null;
        ErrorResponse errors = new ErrorResponse();
        errors.setTimestamp(LocalDateTime.now());
        errors.setError(ex.getMessage());
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof BadRequestException) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        errors.setStatus(httpStatus.value());
        responseEntity = new ResponseEntity<>(errors, httpStatus);
        return responseEntity;
    }

    // error handle for @Valid
    @Override
    protected ResponseEntity<Object>
    handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                 HttpHeaders headers,
                                 HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        //Get all fields errors
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, status);
    }
}
