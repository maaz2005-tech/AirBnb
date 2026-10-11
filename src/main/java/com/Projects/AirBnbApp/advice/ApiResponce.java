package com.Projects.AirBnbApp.advice;

import lombok.Data;

import java.time.LocalDateTime;

@Data

public class ApiResponce<T> {
//    @JsonFormat(pattern = "hh-mm-ss dd-mm-yyyy")


    private LocalDateTime timeStamp;
    private T data;
    private ApiError error;

    public ApiResponce() {
        this.timeStamp = LocalDateTime.now();
    }

    public ApiResponce(T data) {
        this();
        this.data = data;
    }

    public ApiResponce(ApiError error) {
        this();
        this.error = error;
    }
}
