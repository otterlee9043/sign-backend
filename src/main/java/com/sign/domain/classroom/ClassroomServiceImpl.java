package com.sign.domain.classroom;

import com.sign.domain.member.LoginMember;
import com.sign.domain.member.Member;
import com.sign.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Transactional
public class ClassroomServiceImpl implements ClassroomService{

    private final MemberRepository memberRepository;
    private final ClassroomRepository classroomRepository;


    @Override
    public Optional<Classroom> findRoomByRoomCode(String roomCode) {
        return classroomRepository.findByCode(roomCode);
    }

    @Override
    public List<Classroom> findRoomByRoomName(String roomName) {
        return classroomRepository.findByName(roomName);
    }

    @Override
    public Optional<Classroom> findRoomByRoomId(Long roomId) {
        Optional<Classroom> result = classroomRepository.findById(roomId);
        return result;
    }

    @Override
    public Set<Classroom> findJoiningRooms(Member member) {
        return member.getJoiningRooms();
    }


    @Override
    public List<Classroom> findHostingRooms(Member host) {
        return classroomRepository.findByHost(host);
    }

    @Override
    public Classroom createRoom(ClassroomCreateForm form, @AuthenticationPrincipal LoginMember loginMember) {
        Classroom classroom = new Classroom();
        classroom.setRoomCode(form.getRoomCode());
        classroom.setRoomName(form.getRoomName());
        classroom.setHost(loginMember.getMember());
        Classroom created = classroomRepository.save(classroom);
        joinRoom(loginMember.getMember(), created);
//        joinRoom(loginMember.getMember(), classroom.getRoomCode());
        return classroom;
    }

    @Override
    public Classroom joinRoom(Member member, Classroom classroom) {
//        Optional<Classroom> result = classroomRepository.findByCode(roomCode);
//        if (result.isPresent()){
//            Classroom classroom = result.get();
//            member.addJoiningRoom(classroom);
//            memberRepository.save(member);
//        }
        member.addJoiningRoom(classroom);
        memberRepository.save(member);
        return classroom;
    }



    @Override
    public void deleteRoom(Classroom classroom) {
        classroomRepository.delete(classroom);
    }

}
