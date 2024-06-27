package com.betvictor.loremipsum.processing.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<String> handleInvalidParameterException(InvalidParameterException invalidParameterException) {
        log.error("Invalid input parameter provided: {}", invalidParameterException.getMessage(), invalidParameterException);
        return ResponseEntity
                .badRequest()
                .body(invalidParameterException.getMessage());
    }

    @ExceptionHandler(MostFrequentWordException.class)
    public ResponseEntity<String> handleInvalidParameterException(MostFrequentWordException mostFrequentWordException) {
        log.error("Invalid input parameter provided: {}", mostFrequentWordException.getMessage(), mostFrequentWordException);
        return ResponseEntity
                .internalServerError()
                .body(mostFrequentWordException.getMessage());
    }

    @ExceptionHandler(ApiCallException.class)
    public ResponseEntity<String> handleApiCallException(ApiCallException apiCallException) {
        log.error("Something went wrong while fetching Lorem Ipsum api: {}", apiCallException.getMessage());
        return ResponseEntity
                .internalServerError()
                .body("Too many requests. Please try again later or try to reduce the number of requested paragraphs (p).");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Something went wrong: {}", e.getMessage(), e);
        return ResponseEntity
                .internalServerError()
                .body("Something went wrong while processing your request. Please try again later.");
    }
    
}
