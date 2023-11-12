package com.sign.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice(basePackages = "com.sign.global")
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.warn("IllegalArgumentException occurred. Message: {}", e.getMessage());
        return ErrorResult.build("BAD REQUEST", e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler
    public ErrorResult invalidRefreshTokenException(InvalidRefreshTokenException e) {
        log.warn("InvalidRefreshTokenException occurred. Message: {}", e.getMessage());
        return ErrorResult.build("NOT LOGGED IN", e.getMessage());
    }
}
