package com.sign.global.security.authentication;

import com.sign.domain.member.entity.Member;
import com.sign.domain.member.repository.MemberRepository;
import com.sign.global.security.authentication.DefaultLoginService;
import com.sign.global.security.authentication.LoginMember;
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
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
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
    private final String accessTokenHeader = "Authorization";

    private final String refreshTokenHeader = "Refresh-Token";

    private final DefaultLoginService userDetailService;

    private final MemberRepository memberRepository;

    @Value("${jwt.secret.key}")
    private String salt;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    private Key secretKey;


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


    public void saveAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }


    public String extractAccessToken(HttpServletRequest request) {
        String accessToken = request.getHeader(accessTokenHeader);
        if (accessToken == null) {
            throw new IllegalArgumentException("Access Token 존재하지 않음");
        }
        return accessToken;
    }


    public String extractRefreshToken(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(refreshTokenHeader))
                .map(Cookie::getValue)
                .filter(this::isTokenValid)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 Refresh-Token 쿠키"));
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
        response.setHeader(accessTokenHeader, "Bearer " + accessToken);
    }


    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Access-Control-Expose-Headers", accessTokenHeader);
        response.setHeader(accessTokenHeader, "Bearer " + accessToken);

        Cookie cookie = new Cookie(refreshTokenHeader, refreshToken);
        cookie.setMaxAge(refreshTokenExpirationPeriod.intValue());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    public String reissueAccessToken(String refreshToken) {
        Member refreshTokenOwner = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException("해당 Refresh Token으로 사용자 찾을 수 없음."));
        return createAccessToken(refreshTokenOwner.getEmail());
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
}
