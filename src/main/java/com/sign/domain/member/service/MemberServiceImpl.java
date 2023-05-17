package com.sign.domain.member.service;

import com.sign.domain.member.controller.dto.LoginRequest;
import com.sign.domain.member.controller.dto.LoginResponse;
import com.sign.domain.member.entity.Authority;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.exception.DataDuplicateException;
import com.sign.domain.member.repository.MemberRepository;
import com.sign.domain.member.controller.dto.SignupRequest;
import com.sign.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public void join(SignupRequest request) throws Exception {
        Member member = Member.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()))
                .build();
        if (isUsernameExist(request.getUsername())) {
            List<String> fields = Arrays.asList("username");
            if (isEmailExist(request.getEmail())){
                fields.add("email");
            }
            throw new DataDuplicateException("중복된 입력값", fields);
        }
        memberRepository.save(member);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByUsername(request.getUsername()).orElseThrow(() ->
                new BadCredentialsException("존재하지 않는 계정입니다."));
        if(!passwordEncoder.matches(request.getPassword(), member.getPassword())){
            throw new BadCredentialsException("잘못된 비밀번호입니다.");
        }

        return LoginResponse.builder()
                .roles(member.getRoles())
                .token(jwtProvider.createToken(member.getUsername(), member.getRoles()))
                .build();
    }

    @Override
    public Optional<Member> findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    @Override
    public boolean isUsernameExist(String username) {
        return memberRepository.findByUsername(username).isPresent();
    }

    @Override
    public boolean isEmailExist(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
}
