package com.sign.domain.member;

import com.sign.domain.classroom.ClassroomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    private final ClassroomService classroomService;

    @GetMapping("/join")
    public String signupForm(){
        log.info("GET join");
        return "ok";
    }

    @PostMapping("/join")
    public String signup(@Valid MemberSignupForm form, BindingResult bindingResult){
        log.info("signup!");
        log.info("form={}", form);
        if (bindingResult.hasErrors()){
            log.info("errors={}", bindingResult.getAllErrors());
            return "잘못된 양식입니다.";
        }
        if (!form.getPassword1().equals(form.getPassword2())){
            bindingResult.rejectValue("password2", "passwordIncorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "비밀번호가 일치하지 않습니다.";
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
//    public String createRoom(@ModelAttribute ClassroomDTO classroomDTO){
//        classroomService.createRoom(classroomDTO.toEntity());
//        return "room created";
//    }
}

