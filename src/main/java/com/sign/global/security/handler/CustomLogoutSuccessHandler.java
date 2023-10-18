package com.sign.global.security.handler;

import com.sign.domain.member.entity.Member;
import com.sign.global.security.authentication.JwtProvider;
import com.sign.global.security.authentication.LoginMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) {
        log.info("logout | authentication: {}", authentication);
        LoginMember loginMember = (LoginMember) authentication.getPrincipal();

        Member member = loginMember.getMember();
        member.updateRefreshToken("");
        jwtProvider.revokeRefreshToken(response);
    }
}
