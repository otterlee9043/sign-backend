package com.sign.global.security.filter;

import com.sign.global.security.authentication.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    private final RequestMatcher noCheckRequestMatcher;


    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        String[] excludedUris = {
                "/api/v1/members",
                "/api/v1/members/email/*/duplication",
                "/api/v1/refresh/access-token",
                "/css/**",
                "/images/**",
                "/js/**",
                "/favicon.ico",
                "/ws/**",
        };

        AntPathRequestMatcher[] matchers = Arrays.stream(excludedUris)
                .map(AntPathRequestMatcher::new)
                .toArray(AntPathRequestMatcher[]::new);

        this.noCheckRequestMatcher = new OrRequestMatcher(matchers);
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (noCheckRequestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtProvider.extractAccessToken(request);
        Authentication authentication = jwtProvider.getAuthentication(accessToken);
        jwtProvider.saveAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
