package com.sign.domain.member.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ErrorResult {
    private String code;
    private String message;
}
