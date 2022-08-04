package com.sign.member;

import com.sign.domain.classroom.Classroom;
import com.sign.domain.classroom.ClassroomDTO;
import com.sign.domain.classroom.ClassroomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final ClassroomService classroomService;
    @GetMapping("/api/member/join")
    public String member(){
//        Member member = new Member();
//        member.setUsername("spring");
        Member member = memberService.findMember(1L).get();
        memberService.join(member);
//        Optional<Member> result = memberService.findMember(1L);
//        Member member = result.get();


        Classroom classroom = classroomService.findRoomByRoomCode("code2").get();
        classroomService.joinRoom(member, "code2");

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

    @PostMapping("api/room")
    public String createRoom(@ModelAttribute ClassroomDTO classroomDTO){
        classroomService.createRoom(classroomDTO.toEntity());
        return "room created";
    }
}

