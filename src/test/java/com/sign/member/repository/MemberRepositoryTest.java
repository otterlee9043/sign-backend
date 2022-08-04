package com.sign.member.repository;

import com.sign.domain.classroom.Classroom;
import com.sign.domain.classroom.ClassroomService;
import com.sign.member.Member;
import com.sign.member.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import java.util.List;

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
        Member member = new Member();
        member.setUsername("spring");
        memberService.join(member);

        Classroom classroom = new Classroom();
        classroom.setRoomName("new room");
        classroom.setRoomCode("code0");
        classroom.setHost(member);
        classroomService.createRoom(classroom);
        classroomService.joinRoom(member, "code0");
        List<Member> joiningMembers = classroom.getJoiningMembers();
        List<Classroom> joiningRooms = member.getJoiningRooms();
        for (Classroom joiningRoom : joiningRooms) {
            System.out.println("joiningRoom = " + joiningRoom);
        }
        for (Member joiningMember : joiningMembers) {
            System.out.println("joiningMember = " + joiningMember);
        }
    }
}