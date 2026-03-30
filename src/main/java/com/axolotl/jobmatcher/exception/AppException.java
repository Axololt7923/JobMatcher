package com.axolotl.jobmatcher.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class AppException extends RuntimeException {

    private final HttpStatusCode status;

    public AppException(String message, HttpStatusCode status) {
        super(message);
        this.status = status;
    }
}