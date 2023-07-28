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
}
