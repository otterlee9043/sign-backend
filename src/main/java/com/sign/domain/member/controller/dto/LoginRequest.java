package com.sign.domain.member.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class LoginRequest {

    private String username;
    private String password;
}
