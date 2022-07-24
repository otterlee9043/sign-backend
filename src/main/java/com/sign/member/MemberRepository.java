package com.sign.member;

import java.util.List;

public interface MemberRepository {

    Member save(Member member);

    Member findById(Long memberId);

    List<Member> findAll();
}
