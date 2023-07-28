package com.sign.global.security.handler;

import com.sign.domain.member.repository.MemberRepository;
import com.sign.global.security.authentication.JwtProvider;
import com.sign.global.security.authentication.LoginMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

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
                                        Authentication authentication) throws IOException {
        LoginMember loginMember = (LoginMember) authentication.getPrincipal();

        String accessToken = jwtProvider.createAccessToken(loginMember.getUsername());
        String refreshToken = jwtProvider.createRefreshToken();

        jwtProvider.sendRefreshToken(response, refreshToken);
        jwtProvider.updateRefreshToken(loginMember.getUsername(), refreshToken);
        String redirectURI = UriComponentsBuilder.fromUriString("http://localhost:3000/home")
                .queryParam("token", accessToken)
                .build().toUriString();
        response.sendRedirect(redirectURI);
        log.info("User {} logged in successfully via {} from {}",
                loginMember.getMember().getId(), loginMember.getMember().getProvider(), request.getRemoteAddr());
    }
}