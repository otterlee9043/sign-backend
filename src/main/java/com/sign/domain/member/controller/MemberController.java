package com.sign.domain.member.controller;

import com.sign.domain.classroom.service.RoomService;
import com.sign.domain.member.entity.LoginMember;
import com.sign.domain.member.service.MemberService;
import com.sign.domain.member.service.dto.MemberSignupForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    private final RoomService classroomService;

    @GetMapping("/join")
    public String signupForm(){
        log.info("GET join");
        return "ok";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult exHandler(Exception e){
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("BAD", e.getMessage());
    }

    @PostMapping("/join")
    public String signup(@Valid MemberSignupForm form, BindingResult bindingResult){
        log.info("signup!");
        log.info("form={}", form);
        if (bindingResult.hasErrors()){
            log.info("errors={}", bindingResult.getAllErrors());
            throw new RuntimeException("잘못된 양식입니다.");
        }
        if (!form.getPassword1().equals(form.getPassword2())){
            bindingResult.rejectValue("password2", "passwordIncorrect", "2개의 패스워드가 일치하지 않습니다.");
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        memberService.join(form);

        return "successfully joined!";
    }

    @GetMapping("/login")
    public String loginForm(){
        return "GET login";
    }


    @GetMapping("/username")
    public String username(@AuthenticationPrincipal LoginMember loginMember){
        if (loginMember != null){
            return loginMember.getUsername();
        }
        return "expired";
    }

//    @PostMapping("api/room")
//    public String createRoom(@ModelAttribute RoomDTO classroomDTO){
//        classroomService.createRoom(classroomDTO.toEntity());
//        return "room created";
//    }
}

