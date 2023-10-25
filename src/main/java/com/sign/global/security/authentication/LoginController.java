package com.sign.global.security.authentication;

import com.sign.domain.member.entity.Member;
import com.sign.global.security.authentication.oauth2.OAuth2LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class LoginController {

    private final OAuth2LoginService oAuth2LoginService;

    private final JwtProvider jwtProvider;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/oauth2/authorization/{provider}")
    public void authorized(@PathVariable String provider, HttpServletResponse response)
            throws IOException {
        String authorizationURI = oAuth2LoginService.getAuthorizationURI(provider);
        response.sendRedirect(authorizationURI);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/login/oauth2/code/{provider}")
    public void login(@PathVariable String provider, @RequestParam String code,
                      HttpServletResponse response) {
        log.info("/login/oauth2/code/{}", provider);
        Member member = oAuth2LoginService.login(provider, code);
        oAuth2LoginService.sendAccessTokenAndRefreshToken(member, response);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal LoginMember loginMember, HttpServletResponse response) {
        loginMember.getMember().updateRefreshToken("");
        jwtProvider.revokeRefreshToken(response);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/refresh/access-token")
    public void refreshAccessToken(HttpServletRequest request,
                                   HttpServletResponse response) {
        String refreshToken = jwtProvider.extractRefreshToken(request);
        String reissuedAccessToken = jwtProvider.reissueAccessToken(refreshToken);
        jwtProvider.sendAccessToken(response, reissuedAccessToken);
    }

}
