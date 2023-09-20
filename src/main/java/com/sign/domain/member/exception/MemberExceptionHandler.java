package com.sign.domain.member.exception;

import com.sign.global.exception.DataDuplicateException;
import com.sign.global.exception.ErrorResult;
import com.sign.global.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.sign.domain.member")
public class MemberExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult exHandler(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException occurred. Message: {}", e.getMessage());
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors()
                .forEach((error) -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });
        return ErrorResult.builder()
                .code("BAD")
                .message("잘못된 양식")
                .errors(errors)
                .build();
    }


    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResult dataDuplicateExceptionHandler(DataDuplicateException e) {
        log.warn("DataDuplicateException occurred. Message: {}", e.getMessage());
        return ErrorResult.builder()
                .code("CONFLICT")
                .message("이미 존재하는 사용자입니다.")
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResult NotFoundExceptionHandler(NotFoundException e) {
        log.warn("NotFoundException occurred. Message: {}", e.getMessage());
        return ErrorResult.builder()
                .code("NOT FOUND")
                .message("존재하지 않는 사용자입니다.")
                .build();
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public ErrorResult AccessDeniedExceptionHandler(AccessDeniedException e) {
        log.warn("AccessDeniedException occurred. Message: {}", e.getMessage());
        return ErrorResult.builder()
                .code("FORBIDDEN")
                .message(e.getMessage())
                .build();
    }
}
