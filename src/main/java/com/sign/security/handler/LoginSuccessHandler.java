package com.sign.security.handler;

import com.sign.domain.member.repository.MemberRepository;
import com.sign.security.JwtProvider;
import com.sign.security.LoginMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        LoginMember loginMember = (LoginMember) authentication.getPrincipal();
        String email = loginMember.getUsername();
        String token = jwtProvider.createToken(email, loginMember.getMember().getRole());
        jwtProvider.sendToken(response, token);
        log.info("로그인에 성공!!");
    }
}
