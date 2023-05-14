package com.sign.domain.member.controller;

import com.sign.domain.classroom.service.RoomService;
import com.sign.domain.member.security.LoginMember;
import com.sign.domain.member.exception.DataDuplicateException;
import com.sign.domain.member.service.MemberService;
import com.sign.domain.member.controller.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        Map<String, Object> result = new HashMap<>();

        memberService.join(request);
        log.info("member {} joined", request.getUsername());
        return new ResponseEntity<>(result, HttpStatus.OK);
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

