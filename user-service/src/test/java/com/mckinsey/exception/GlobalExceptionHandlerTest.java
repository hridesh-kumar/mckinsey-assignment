package com.mckinsey.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleException() {
        AppException appException = new AppException("Test exception message");
        WebRequest webRequest = mock(WebRequest.class);

        ResponseEntity<Map<String, Object>> responseEntity = globalExceptionHandler.handleException(appException, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Failed", responseEntity.getBody().get("status"));
        assertEquals("Test exception message", responseEntity.getBody().get("message"));
        assertEquals(LocalDateTime.class, responseEntity.getBody().get("timestamp").getClass());
    }
}
