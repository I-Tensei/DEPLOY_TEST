package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全コントローラ共通の例外ハンドリング。
 * 500 Whitelabel の原因特定を容易にするため、JSON でスタックの概要を返す。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, Object> body = baseBody(req, HttpStatus.BAD_REQUEST);
        body.put("error", "ValidationError");
        body.put("messages", ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.toList()));
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex, HttpServletRequest req) {
        log.error("RuntimeException: {}", ex.getMessage(), ex);
        Map<String, Object> body = baseBody(req, HttpStatus.INTERNAL_SERVER_ERROR);
        body.put("error", ex.getClass().getSimpleName());
        body.put("message", ex.getMessage());
        body.put("cause", ex.getCause() != null ? ex.getCause().toString() : null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        Map<String, Object> body = baseBody(req, HttpStatus.INTERNAL_SERVER_ERROR);
        body.put("error", ex.getClass().getSimpleName());
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private Map<String, Object> baseBody(HttpServletRequest req, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("path", req.getRequestURI());
        return body;
    }
}
