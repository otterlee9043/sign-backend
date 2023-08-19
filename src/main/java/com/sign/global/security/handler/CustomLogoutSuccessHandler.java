package com.sign.global.security.handler;

import com.sign.domain.member.entity.Member;
import com.sign.global.security.authentication.JwtProvider;
import com.sign.global.security.authentication.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) {
        LoginMember loginMember = (LoginMember) authentication.getPrincipal();
        Member member = loginMember.getMember();
        member.updateRefreshToken("");
        jwtProvider.revokeRefreshToken(response);
    }
}
