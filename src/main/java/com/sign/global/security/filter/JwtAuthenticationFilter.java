package com.sign.global.security.filter;

import com.sign.domain.member.entity.Member;
import com.sign.domain.member.repository.MemberRepository;
import com.sign.global.security.authentication.JwtProvider;
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
    private final MemberRepository memberRepository;
    private final RequestMatcher noCheckRequestMatcher;


    public JwtAuthenticationFilter(JwtProvider jwtProvider, MemberRepository memberRepository) {
        this.jwtProvider = jwtProvider;
        this.memberRepository = memberRepository;
        String[] excludedUris = {
                "/api/v1/members",
                "/api/v1/member/login",
                "/api/v1/member/username/*/exists",
                "/api/v1/members/email/*/duplication",
                "/oauth2/authorization/**",
                "/login/oauth2/code/**",
                "/css/**",
                "/images/**",
                "/js/**",
                "/favicon.ico",
                "/swagger-ui/**",
                "/v3/api-docs/**"
        };

        AntPathRequestMatcher[] matchers = Arrays.stream(excludedUris)
                .map(AntPathRequestMatcher::new)
                .toArray(AntPathRequestMatcher[]::new);

        this.noCheckRequestMatcher = new OrRequestMatcher(matchers);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (noCheckRequestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = jwtProvider.extractAccessToken(request)
                .filter(jwtProvider::isTokenValid)
                .orElse(null);

        if (accessToken != null) {
            Authentication authentication = jwtProvider.getAuthentication(accessToken);
            jwtProvider.saveAuthentication(authentication);
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = jwtProvider.extractRefreshToken(request)
                .filter(jwtProvider::isTokenValid)
                .orElse(null);

        if (refreshToken != null) {
            Member refreshTokenOwner = memberRepository.findByRefreshToken(refreshToken).orElse(null);
            if (refreshTokenOwner != null) {
                String username = refreshTokenOwner.getEmail();
                jwtProvider.sendAccessToken(response, jwtProvider.createAccessToken(username));
                Authentication authentication = jwtProvider.getAuthentication(refreshTokenOwner);
                jwtProvider.saveAuthentication(authentication);
                filterChain.doFilter(request, response);
            }
        }
    }
}
