package me.ifmo.backend.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import me.ifmo.backend.DTO.responses.ApiErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleNotFound(NotFoundException exception, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleConflict(ConflictException exception, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, exception.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleBadRequest(IllegalArgumentException exception, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDTO> handleOther(Exception exception, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), req.getRequestURI());
    }

    private ResponseEntity<ApiErrorResponseDTO> build(HttpStatus status, String message, String path) {
        ApiErrorResponseDTO body = new ApiErrorResponseDTO(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
        return ResponseEntity.status(status).body(body);
    }
}