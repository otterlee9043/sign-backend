package com.sign.domain.classroom.exception;

import com.sign.global.exception.DataDuplicateException;
import com.sign.global.exception.ErrorResult;
import com.sign.global.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        return ErrorResult.builder()
                .code("NOT FOUND")
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResult roomCapacityExceededExceptionHandler(RoomCapacityExceededException e) {
        log.warn("RoomCapacityExceededException occurred. Message: {}", e.getMessage());
        return ErrorResult.builder()
                .code("ROOM CAPACITY EXCEEDED")
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResult dataDuplicateExceptionHandler(DataDuplicateException e) {
        log.warn("DataDuplicateException occurred. Message: {}", e.getMessage());
        return ErrorResult.builder()
                .code("CONFLICT")
                .message("이미 존재하는 방입니다.")
                .build();
    }
}
