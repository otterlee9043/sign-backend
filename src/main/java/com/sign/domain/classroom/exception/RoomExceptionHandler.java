package com.sign.domain.classroom.exception;

import com.sign.global.exception.DataDuplicateException;
import com.sign.global.exception.ErrorResult;
import com.sign.global.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.sign.domain.classroom")
public class RoomExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResult roomNotFoundExceptionHandler(NotFoundException e) {
        log.warn("NotFoundException occurred. Message: {}", e.getMessage());
        return ErrorResult.build("NOT FOUND", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResult roomCapacityExceededExceptionHandler(RoomCapacityExceededException e) {
        log.warn("RoomCapacityExceededException occurred. Message: {}", e.getMessage());
        return ErrorResult.build("ROOM CAPACITY EXCEEDED", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResult dataDuplicateExceptionHandler(DataDuplicateException e) {
        log.warn("DataDuplicateException occurred. Message: {}", e.getMessage());
        return ErrorResult.build("CONFLICT", e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public ErrorResult accessDeniedExceptionHandler(AccessDeniedException e) {
        log.warn("AccessDeniedException occurred. Message: {}", e.getMessage());
        return ErrorResult.build("FORBIDDEN", e.getMessage());
    }

}
