package com.sign.global.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class ErrorResult {
    private String code;
    private String message;
    private Map<String, String> errors;

    public static ErrorResult build(String code, String message) {
        return ErrorResult.builder()
                .code(code)
                .message(message)
                .build();
    }

    public static ErrorResult build(String code, String message, Map<String, String> errors) {
        return ErrorResult.builder()
                .code(code)
                .message(message)
                .errors(errors)
                .build();
    }
}
