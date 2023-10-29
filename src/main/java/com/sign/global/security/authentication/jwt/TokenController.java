package com.sign.global.security.authentication.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class TokenController {

    private final JwtProvider jwtProvider;


    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/refresh/access-token")
    public void refreshAccessToken(HttpServletRequest request,
                                                     HttpServletResponse response) {
        String refreshToken = jwtProvider.extractRefreshToken(request);
        String reissuedAccessToken = jwtProvider.reissueAccessToken(refreshToken);
        jwtProvider.sendAccessToken(response, reissuedAccessToken);
    }

}
