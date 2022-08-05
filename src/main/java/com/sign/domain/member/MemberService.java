package com.sign.domain.member;

import java.util.Optional;

public interface MemberService {
    void join(MemberSignupForm form);

    Optional<Member> findMember(Long memberId);
}

