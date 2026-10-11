package com.Projects.AirBnbApp.advice;

import com.Projects.AirBnbApp.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponce<?>> handelResourceNotFoundException(ResourceNotFoundException exception){
        ApiError apiError=ApiError.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .build();
        return builderErrorResponceEntity(apiError);
    }
    private ResponseEntity<ApiResponce<?>> builderErrorResponceEntity(ApiError apiError){
        return new ResponseEntity<>(new ApiResponce<>(apiError),apiError.getStatusCode());
    }
}
