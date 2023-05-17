package com.sign.domain.member.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthErrorResult {
    private String message;
}
