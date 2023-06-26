package com.sign.global.security.handler;

import com.sign.domain.member.repository.MemberRepository;
import com.sign.global.security.authentication.LoginMember;
import com.sign.global.security.authentication.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        LoginMember loginMember = (LoginMember) authentication.getPrincipal();
        String email = loginMember.getUsername();
        String token = jwtProvider.createToken(email, loginMember.getMember().getRole());
        jwtProvider.sendToken(response, token);
        log.info("User {} logged in successfully from {}", loginMember.getMember().getId(), request.getRemoteAddr());
    }
}
