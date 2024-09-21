package com.mckinsey.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;


@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, Object>> handleException
            (AppException exception, WebRequest webRequest) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "Failed");
        body.put("timestamp", LocalDateTime.now());
        body.put("message", exception.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}
