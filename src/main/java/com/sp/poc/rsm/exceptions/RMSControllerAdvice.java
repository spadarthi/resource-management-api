package com.sp.poc.rsm.exceptions;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RMSControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Object> handleInvalidInput(InvalidInputException ex) {
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, "Invalid Input", ex));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, "Employee not found", ex));
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<Object> handleConversionFailed(ConversionFailedException ex) {
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, "Unable to convert", ex));
    }

    @ExceptionHandler(EmployeeStateChangeException.class)
    public ResponseEntity<Object> handleEmployeeStateChange(EmployeeStateChangeException ex) {
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, "Employee state change not allowed", ex));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, "Malformed JSON request", ex));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
