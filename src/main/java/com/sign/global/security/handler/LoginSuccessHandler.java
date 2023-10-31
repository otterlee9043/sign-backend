package com.sign.global.security.handler;

import com.sign.global.security.authentication.LoginMember;
import com.sign.global.security.authentication.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        LoginMember loginMember = (LoginMember) authentication.getPrincipal();
        String email = loginMember.getUsername();
        String accessToken = jwtProvider.createAccessToken(email);
        String refreshToken = jwtProvider.createRefreshToken();

        jwtProvider.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtProvider.updateRefreshToken(email, refreshToken);
        log.info("User {} logged in successfully from {}", loginMember.getMember().getId(), request.getRemoteAddr());
    }
}
