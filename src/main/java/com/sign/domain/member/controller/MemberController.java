package com.sign.domain.member.controller;

import com.sign.domain.classroom.service.RoomService;
import com.sign.domain.member.entity.LoginMember;
import com.sign.domain.member.service.MemberService;
import com.sign.domain.member.controller.dto.MemberSignupErrorResult;
import com.sign.domain.member.controller.dto.MemberSignupForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PersistenceException;
import javax.validation.Valid;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    private final RoomService classroomService;

    @PostMapping("/join")
    public ResponseEntity signup(@RequestBody @Valid MemberSignupForm form){
        log.info("form={}", form);
        Map<String, Object> result = new HashMap<>();
        memberService.join(form);
        log.info("member {} joined", form.getUsername());
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

    @GetMapping("/username")
    public String username(@AuthenticationPrincipal LoginMember loginMember){
        if (loginMember != null){
            return loginMember.getUsername();
        }
        return "expired";
    }

}

