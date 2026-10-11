package com.Projects.AirBnbApp.advice;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ApiError {
//    private LocalDateTime timestamp;
    private String message;
    private HttpStatus statusCode;
    private List<String> subErrors;

//    public ApiError() {
//        this.timestamp = LocalDateTime.now();
//    }

//    public ApiError(String error, HttpStatus statusCode) {
//        this();
//        this.error = error;
//        this.statusCode = statusCode;
//    }
}
