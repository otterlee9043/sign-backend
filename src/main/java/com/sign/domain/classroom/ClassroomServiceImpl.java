package com.sign.domain.classroom;

import com.sign.domain.member.LoginMember;
import com.sign.domain.member.Member;
import com.sign.domain.member.repository.MemberRepository;
import com.sign.domain.websocket.ChatEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class ClassroomServiceImpl implements ClassroomService{

    private final MemberRepository memberRepository;
    private final ClassroomRepository classroomRepository;
    private final ChatEventListener chatEventListener;

    // roomId, sessionId, color
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
        Member memberToJoin = memberRepository.findById(member.getId()).get();
        memberToJoin.addJoiningRoom(classroom);
        log.info("member.getJoiningRooms: {}",memberToJoin.getJoiningRooms());
        memberRepository.save(memberToJoin);
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


    @Override
    public Map<Integer, String> getRoomStates(Integer roomId) {
        return chatEventListener.getRoomStatesByRoomId(roomId);
    }

    @Override
    public Integer getMySeatPosition(Integer roomId, String sessionId) {
        return chatEventListener.getMySeatPosition(roomId, sessionId);
    }
}
