package com.sign.global.security.handler;

import com.sign.domain.member.repository.MemberRepository;
import com.sign.global.security.authentication.JwtProvider;
import com.sign.global.security.authentication.LoginMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        LoginMember loginMember = (LoginMember) authentication.getPrincipal();

        String token = jwtProvider.createToken(loginMember.getUsername(), loginMember.getMember().getRole());
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(1 * 60 * 60);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.sendRedirect("http://localhost:3000/home");
        log.info("User {} logged in successfully via {} from {}",
                loginMember.getMember().getId(), loginMember.getMember().getProvider(), request.getRemoteAddr());
    }
}
