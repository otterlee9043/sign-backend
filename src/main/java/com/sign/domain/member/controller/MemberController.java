package com.sign.domain.member.controller;

import com.sign.domain.member.controller.dto.LoginRequest;
import com.sign.domain.member.controller.dto.LoginResponse;
import com.sign.domain.member.service.MemberService;
import com.sign.domain.member.controller.dto.SignupRequest;
import com.sign.security.LoginMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity signup(@RequestBody @Valid SignupRequest request) throws Exception {
        log.info("form={}", request);
        memberService.join(request);
        log.info("member {} joined", request.getUsername());
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequest request,
                                HttpServletResponse response) {
        LoginResponse tokenResponse = memberService.login(request);
//        ResponseCookie cookie = ResponseCookie.from("token", tokenResponse.getToken())
//                .maxAge(1 * 60 * 60)
//                .path("/")
//                .httpOnly(true)
//                .build();
//        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        Cookie cookie = new Cookie("token", tokenResponse.getToken());
        cookie.setMaxAge(1 * 60 * 60);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
//        log.info("cookie token: {}", response.getHeader("Set-Cookie"));
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @GetMapping("/userInfo")
    public ResponseEntity userInfo(@AuthenticationPrincipal LoginMember loginMember){
        log.info("userInfo | username: {}", loginMember.getUsername());
        Map<String, String> info = Map.of("username", loginMember.getUsername());
        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    @GetMapping("/username/{username}/exists")
    public ResponseEntity checkUserByUsername(@PathVariable String username) {
        Map<String, Object> result = new HashMap<>();
        result.put("duplicate", memberService.isUsernameExist(username));
        log.info("[checkUserByUsername] check {} result {}", username, result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/email/{email}/exists")
    public ResponseEntity checkUserByEmail(@PathVariable String email) {
        Map<String, Object> result = new HashMap<>();
        result.put("duplicate", memberService.isEmailExist(email));
        log.info("[checkUserByEmail] check {} result {}", email, result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}

