package com.sign.global.security.authentication;

import com.sign.domain.member.entity.Member;
import com.sign.domain.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {
    private final String accessTokenHeader = "Access-Token";

    private final String refreshTokenHeader = "Refresh-Token";

    private final MemberSecurityService userDetailService;

    private final MemberRepository memberRepository;

    @Value("${jwt.secret.key}")
    private String salt;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    private Key secretKey;

    public String getAccessTokenHeader() {
        return accessTokenHeader;
    }

    public String getRefreshTokenHeader() {
        return refreshTokenHeader;
    }


    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpirationPeriod))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String accessToken) {
        UserDetails userDetails = userDetailService.loadUserByUsername(this.getUsername(accessToken));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Authentication getAuthentication(Member member) {
        UserDetails userDetails = new LoginMember(member);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public void saveAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(refreshTokenHeader))
                    .map(Cookie::getValue)
                    .findFirst();
        }
        return Optional.empty();
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessTokenHeader));
    }

    public boolean isTokenValid(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Access-Control-Expose-Headers", accessTokenHeader);
        response.setHeader(accessTokenHeader, accessToken);
    }

    public void sendRefreshToken(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(refreshTokenHeader, refreshToken);
        cookie.setMaxAge(refreshTokenExpirationPeriod.intValue());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void updateRefreshToken(String email, String refreshToken) {
        memberRepository.findByEmail(email)
                .ifPresentOrElse(
                        member -> memberRepository.save(member.updateRefreshToken(refreshToken)),
                        () -> new Exception("일치하는 회원이 없습니다.")
                );
    }

    public void revokeRefreshToken(HttpServletResponse response) {
        Cookie cookie = new Cookie(refreshTokenHeader, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Access-Control-Expose-Headers", accessTokenHeader);
        response.setHeader(accessTokenHeader, accessToken);
        Cookie cookie = new Cookie(refreshTokenHeader, refreshToken);
        cookie.setMaxAge(refreshTokenExpirationPeriod.intValue());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }


}
