package com.betvictor.loremipsum.repository.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Something went wrong: {}", e.getMessage(), e);
        return ResponseEntity
                .internalServerError()
                .body("Something went wrong while processing your request. Please try again later.");
    }
    
}
