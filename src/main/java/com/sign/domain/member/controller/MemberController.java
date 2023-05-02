package com.sign.domain.member.controller;

import com.sign.domain.classroom.service.RoomService;
import com.sign.domain.member.entity.LoginMember;
import com.sign.domain.member.service.MemberService;
import com.sign.domain.member.service.dto.MemberSignupErrorResult;
import com.sign.domain.member.service.dto.MemberSignupForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    private final RoomService classroomService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity exHandler(MethodArgumentNotValidException e){
        log.error("[exceptionHandler] ex", e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors()
                .forEach((error) -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });
        MemberSignupErrorResult errorResult = MemberSignupErrorResult.builder()
                .code("BAD")
                .message("잘못된 양식")
                .errors(errors)
                .build();
        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/join")
    public String signup(@RequestBody @Validated MemberSignupForm form){
        log.info("signup!");
        log.info("form={}", form);
        memberService.join(form);

        return "successfully joined!";
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

