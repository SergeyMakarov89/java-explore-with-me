package ru.practicum.ExploreWithMe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HashMap<String, Object> handleNotFoundException(NotFoundException exception) {
        HashMap<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errors", exception.getStackTrace());
        errorResponse.put("message", exception.getMessage());
        errorResponse.put("reason", exception.getMessage());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("timestamp", LocalDateTime.now());

        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HashMap<String, Object> handleValidationException(ValidationException exception) {
        HashMap<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errors", exception.getStackTrace());
        errorResponse.put("message", exception.getMessage());
        errorResponse.put("reason", exception.getMessage());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("timestamp", LocalDateTime.now());

        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public HashMap<String, Object> handleConflictException(ConflictException exception) {
        HashMap<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errors", exception.getStackTrace());
        errorResponse.put("message", exception.getMessage());
        errorResponse.put("reason", exception.getMessage());
        errorResponse.put("status", HttpStatus.CONFLICT.value());
        errorResponse.put("timestamp", LocalDateTime.now());

        return errorResponse;
    }
}
