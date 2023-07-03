package com.sign.domain.member.controller;

import com.sign.domain.member.controller.dto.MemberInfo;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.service.MemberService;
import com.sign.domain.member.controller.dto.SignupRequest;
import com.sign.global.security.authentication.LoginMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/join")
    public void signup(@RequestBody @Valid SignupRequest request) throws Exception {
        memberService.join(request);
    }

    @GetMapping("/userInfo")
    public MemberInfo userInfo(@AuthenticationPrincipal LoginMember loginMember, HttpSession session){
        Member member = loginMember.getMember();
        return MemberInfo.builder()
                .username(member.getUsername())
                .email(member.getEmail())
                .picture(member.getPicture())
                .build();
    }


    @GetMapping("/email/{email}/exists")
    public ResponseEntity checkUserByEmail(@PathVariable String email) {
        Map<String, Object> result = new HashMap<>();
        result.put("duplicate", memberService.doesEmailExist(email));
        log.info("[checkUserByEmail] check {} result {}", email, result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}

