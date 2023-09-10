package com.sign.domain.member.service;

import com.sign.domain.member.controller.dto.SignupRequest;
import com.sign.domain.member.entity.Member;
import com.sign.global.security.authentication.LoginMember;

public interface MemberService {
    void join(SignupRequest request) throws Exception;

    Member findMember(Long memberId);

    void deleteMember(Member member);

    boolean doesEmailExist(String email);

    Member getVerifiedMember(Long memberId, LoginMember loginMember);
}

