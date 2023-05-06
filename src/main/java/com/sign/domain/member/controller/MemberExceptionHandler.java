package com.sign.domain.member.controller;

import com.sign.domain.member.controller.dto.MemberSignupErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public MemberSignupErrorResult exHandler(MethodArgumentNotValidException e){
        log.error("[MethodArgumentNotValidException] ex", e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors()
                .forEach((error) -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });
        MemberSignupErrorResult errorResult = MemberSignupErrorResult.builder()
                .code("BAD")
                .message("잘못된 양식")
                .errors(errors)
                .build();
        return errorResult;
    }


    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public MemberSignupErrorResult dataDuplicateExceptionHandler(ConstraintViolationException e){
        log.error("[dataDuplicateExceptionHandler] ex", e);
        e.getConstraintName()
        Map<String, String> errors = new HashMap<>();
        MemberSignupErrorResult errorResult = MemberSignupErrorResult.builder()
                .code("CONFLICT")
                .message("중복된 입력값")
                .errors(errors)
                .build();
        return errorResult;
    }

}
