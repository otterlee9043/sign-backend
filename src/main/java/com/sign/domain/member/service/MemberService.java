package com.sign.domain.member.service;

import com.sign.domain.member.entity.Member;
import com.sign.domain.member.service.dto.MemberSignupForm;

import java.util.Optional;

public interface MemberService {
    void join(MemberSignupForm form);

    Optional<Member> findMember(Long memberId);
}

