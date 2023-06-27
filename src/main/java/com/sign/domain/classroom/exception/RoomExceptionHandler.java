package com.sign.domain.classroom.exception;

import com.sign.domain.classroom.controller.dto.RoomErrorResult;
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
    public RoomErrorResult roomNotFoundExceptionHandler(NotFoundException e){
        log.warn("NotFoundException occurred. Message: ", e.getMessage());
        RoomErrorResult errorResult = RoomErrorResult.builder()
                .code("NOT FOUND")
                .message(e.getMessage())
                .build();
        return errorResult;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public RoomErrorResult roomCapacityExceededExceptionHandler(RoomCapacityExceededException e){
        log.warn("RoomCapacityExceededException occurred. Message: ", e.getMessage());
        RoomErrorResult errorResult = RoomErrorResult.builder()
                .code("ROOM CAPACITY EXCEEDED")
                .message(e.getMessage())
                .build();
        return errorResult;
    }
}
