package com.sign.domain.classroom;

import com.sign.domain.member.LoginMember;
import com.sign.domain.member.Member;
import com.sign.domain.member.MemberSecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;
    private final UserDetailsService memberSecurityService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/classrooms")
    public Classroom create(@ModelAttribute ClassroomCreateForm form, BindingResult bindingResult,
                            @AuthenticationPrincipal LoginMember loginMember){
        log.info("form={}", form);
        return classroomService.createRoom(form, loginMember);
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("principal : " + authentication.getPrincipal());
//        System.out.println("Implementing class of UserDetails: " + authentication.getPrincipal().getClass());
//        System.out.println("Implementing class of UserDetailsService: " + memberSecurityService.getClass());
//        return null;
    }
}
