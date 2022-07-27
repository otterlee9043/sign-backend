package com.sign.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/api/member/join")
    public String member(@ModelAttribute Member member){
        log.info("member={}", member);
        memberService.join(member);
        Optional<Member> foundMember = memberService.findMember(member.getId());
        log.info("foundMember={}", foundMember);
//        log.info("name={}", foundMember.get().getName());
//        System.out.println("foundMember.get().getName() = [" + foundMember.get().getName() + "]");
        return "ok";
    }

    @GetMapping("/api/member/username")
    public String username(){
        Optional<Member> foundMember = memberService.findMember(8L);
        log.info("foundMember={}", foundMember);
        log.info("name={}", foundMember.get().getUsername());
        System.out.println("foundMember.get().getName() = [" + foundMember.get().getUsername() + "]");
        return foundMember.get().getUsername();
    }
}

