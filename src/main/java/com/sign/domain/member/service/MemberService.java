package com.sign.domain.member.service;

import com.sign.domain.member.controller.dto.LoginRequest;
import com.sign.domain.member.controller.dto.LoginResponse;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.controller.dto.SignupRequest;

import java.util.Optional;

public interface MemberService {
    void join(SignupRequest request) throws Exception;

    LoginResponse login(LoginRequest request);

    Optional<Member> findMember(Long memberId);

    boolean isUsernameExist(String username);

    boolean isEmailExist(String email);
}

