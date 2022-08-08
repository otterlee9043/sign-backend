package com.sign.domain.classroom;

import com.sign.domain.member.LoginMember;
import com.sign.domain.member.Member;
import com.sign.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
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
    public Classroom createRoom(ClassroomCreateForm form, LoginMember loginMember) {
        //log.info("loginMember.getMember(): {}", loginMember.getMember());
        Member host = memberRepository.findById(loginMember.getMember().getId()).get();
        Classroom classroom = new Classroom();
        classroom.setRoomCode(form.getRoomCode());
        classroom.setRoomName(form.getRoomName());
        classroom.setHost(host);
        Classroom created = classroomRepository.save(classroom);
        joinRoom(host, created);
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
        log.info("member.getJoiningRooms: {}",member.getJoiningRooms());
        memberRepository.save(member);
        return classroom;
    }

    @Override
    public boolean checkJoined(Member member, Classroom classroom) {
        Member checkingMember = memberRepository.findById(member.getId()).get();
        if (findJoiningRooms(checkingMember).contains(classroom)) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteRoom(Classroom classroom) {
        classroomRepository.delete(classroom);
    }

}
