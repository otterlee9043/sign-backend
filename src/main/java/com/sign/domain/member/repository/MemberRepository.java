package com.sign.domain.member.repository;

import com.sign.domain.classroom.entity.Room;
import com.sign.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

//    Member save(Member member);
//
//    Optional<Member> findById(Long memberId);
//
//    List<Member> findAll();

    Optional<Member> findByUsername(String username);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByRefreshToken(String refreshToken);

}
