
package com.Projects.AirBnbApp.advice;

import com.Projects.AirBnbApp.exception.ResourceNotFoundException;
import io.jsonwebtoken.JwtException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponce<?>> handleResourceNotFoundException(
            ResourceNotFoundException exception) {

        ApiError apiError = ApiError.builder()
                .statusCode(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .build();

        return builderErrorResponseEntity(apiError);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponce<?>> handleAuthenticationException(
            AuthenticationException ex) {

        ApiError apiError = ApiError.builder()
                .statusCode(HttpStatus.UNAUTHORIZED)
                .message(ex.getMessage())
                .build();

        return builderErrorResponseEntity(apiError);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponce<?>> handleJwtException(
            JwtException ex) {

        ApiError apiError = ApiError.builder()
                .statusCode(HttpStatus.UNAUTHORIZED)
                .message(ex.getMessage())
                .build();

        return builderErrorResponseEntity(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponce<?>> handleAccessDeniedException(
            AccessDeniedException ex) {

        ApiError apiError = ApiError.builder()
                .statusCode(HttpStatus.FORBIDDEN)
                .message(ex.getMessage())
                .build();

        return builderErrorResponseEntity(apiError);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponce<?>> handleIllegalStateException(
            IllegalStateException ex) {

        ApiError apiError = ApiError.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.CONFLICT)
                .build();

        return builderErrorResponseEntity(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponce<?>> handleInternalServerError(
            Exception exception) {

        ApiError apiError = ApiError.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("An unexpected error occurred")
                .build();

        // Log the actual exception for debugging.
        exception.printStackTrace();

        return builderErrorResponseEntity(apiError);
    }

    private ResponseEntity<ApiResponce<?>> builderErrorResponseEntity(
            ApiError apiError) {

        return ResponseEntity
                .status(apiError.getStatusCode())
                .body(new ApiResponce<>(apiError));
    }
}
