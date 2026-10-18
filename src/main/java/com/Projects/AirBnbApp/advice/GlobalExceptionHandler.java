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
//    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponce<?>> handelResourceNotFoundException(ResourceNotFoundException exception){
        ApiError apiError=ApiError.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .build();
        return builderErrorResponceEntity(apiError);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponce<?>> handleAuthenticationException(AuthenticationException ex){
        ApiError apiError=ApiError.builder()
                .statusCode(HttpStatus.UNAUTHORIZED)
                .message(ex.getMessage())
                .build();
        return builderErrorResponceEntity(apiError);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponce<?>> handleJwtException(JwtException ex){
        ApiError apiError=ApiError.builder()
                .statusCode(HttpStatus.UNAUTHORIZED)
                .message(ex.getMessage())
                .build();
        return builderErrorResponceEntity(apiError);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponce<?>> handleAccessDeniedException(AccessDeniedException ex){
        ApiError apiError=ApiError.builder()
                .statusCode(HttpStatus.FORBIDDEN)
                .message(ex.getMessage())
                .build();
        return builderErrorResponceEntity(apiError);
    }

    public ResponseEntity<ApiResponce<?>> handleInternalServerError(Exception exception) {
        ApiError apiError = ApiError.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .build();
        return builderErrorResponceEntity(apiError);
    }

    private ResponseEntity<ApiResponce<?>> builderErrorResponceEntity(ApiError apiError){
        return new ResponseEntity<>(new ApiResponce<>(apiError),apiError.getStatusCode());
    }
}
