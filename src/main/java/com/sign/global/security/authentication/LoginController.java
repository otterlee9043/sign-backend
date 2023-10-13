package com.sign.global.security.authentication;

import com.sign.domain.member.entity.Member;
import com.sign.global.security.authentication.oauth2.OAuth2LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LoginController {

    private final OAuth2LoginService oAuth2LoginService;

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

}
