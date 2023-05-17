package com.sign.domain.member.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class SignupErrorResult {
    private String code;
    private String message;
    private Map<String, String> errors;
}
