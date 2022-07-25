package com.sign.member.repository;

import com.sign.member.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(Long memberId);

    List<Member> findAll();
}
