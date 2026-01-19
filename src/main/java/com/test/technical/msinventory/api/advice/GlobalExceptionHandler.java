package com.test.technical.msinventory.api.advice;


import com.test.technical.msinventory.api.dto.JsonApiError;
import com.test.technical.msinventory.api.dto.JsonApiErrorResponse;
import com.test.technical.msinventory.exception.BadRequestException;
import com.test.technical.msinventory.exception.CommunicationException;
import com.test.technical.msinventory.exception.ConflictException;
import com.test.technical.msinventory.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<JsonApiErrorResponse> handleBadRequest(BadRequestException ex) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<JsonApiErrorResponse> handleNotFound(NotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<JsonApiErrorResponse> handleConflict(ConflictException ex) {
        return build(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    @ExceptionHandler(CommunicationException.class)
    public ResponseEntity<JsonApiErrorResponse> handleCommunication(CommunicationException ex) {
        log.warn("Communication error: {}", ex.getMessage(), ex);
        return build(HttpStatus.BAD_GATEWAY, "Bad Gateway", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<JsonApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        return build(HttpStatus.BAD_REQUEST, "Validation Error", detail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<JsonApiErrorResponse> handleIllegalArg(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<JsonApiErrorResponse> handleIllegalState(IllegalStateException ex) {
        return build(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonApiErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Unexpected error occurred");
    }

    private ResponseEntity<JsonApiErrorResponse> build(HttpStatus status, String title, String detail) {
        JsonApiError error = new JsonApiError(String.valueOf(status.value()), title, detail);
        return ResponseEntity.status(status).body(new JsonApiErrorResponse(List.of(error)));
    }

}
