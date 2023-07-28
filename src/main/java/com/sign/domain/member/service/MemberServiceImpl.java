package com.sign.domain.member.service;

import com.sign.domain.member.Role;
import com.sign.domain.member.entity.Member;
import com.sign.global.exception.DataDuplicateException;
import com.sign.domain.member.repository.MemberRepository;
import com.sign.domain.member.controller.dto.SignupRequest;
import com.sign.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void join(SignupRequest request) {
        Member member = Member.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .provider("sign")
                .build();
        if (doesUsernameExist(request.getUsername())) {
            List<String> fields = Arrays.asList("username");
            if (doesEmailExist(request.getEmail())){
                fields.add("email");
            }
            throw new DataDuplicateException("중복된 입력값", fields);
        }
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException("Member does not exist."));
    }

    @Override
    public void deleteMember(Member member) {
        memberRepository.delete(member);
    }

    @Override
    public boolean doesUsernameExist(String username) {
        return memberRepository.findByUsername(username).isPresent();
    }

    @Override
    public boolean doesEmailExist(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
}
