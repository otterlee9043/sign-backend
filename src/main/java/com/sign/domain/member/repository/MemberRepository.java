package com.sign.domain.member.repository;

import com.sign.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(Long memberId);

    List<Member> findAll();

    Optional<Member> findByUsername(String username);

    boolean checkAttached(Member member);
}
