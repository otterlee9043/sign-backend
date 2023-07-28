package com.sign.domain.member.service;

import com.sign.domain.member.entity.Member;
import com.sign.domain.member.controller.dto.SignupRequest;

import java.util.Optional;

public interface MemberService {
    void join(SignupRequest request) throws Exception;

    Member findMember(Long memberId);

    void deleteMember(Member member);
    boolean doesUsernameExist(String username);

    boolean doesEmailExist(String email);
}

