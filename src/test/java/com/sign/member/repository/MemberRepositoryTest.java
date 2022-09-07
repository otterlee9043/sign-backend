package com.sign.member.repository;

import com.sign.domain.classroom.service.ClassroomService;
import com.sign.domain.member.service.MemberService;
import com.sign.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
class MemberRepositoryTest {

    @Autowired
    MemberRepository repository;

    @Autowired
    MemberService memberService;
    @Autowired
    ClassroomService classroomService;
    @Test
    public void save(){
//        Member member = new Member();
//        member.setUsername("spring");
//        memberService.join(member);
//
//        Classroom classroom = new Classroom();
//        classroom.setRoomName("new room");
//        classroom.setRoomCode("code0");
//        classroom.setHost(member);
//        classroomService.createRoom(classroom);
//        classroomService.joinRoom(member, "code0");
//        List<Member> joiningMembers = classroom.getJoiningMembers();
//        List<Classroom> joiningRooms = member.getJoiningRooms();
//        for (Classroom joiningRoom : joiningRooms) {
//            System.out.println("joiningRoom = " + joiningRoom);
//        }
//        for (Member joiningMember : joiningMembers) {
//            System.out.println("joiningMember = " + joiningMember);
//        }
    }
}