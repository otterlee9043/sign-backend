package com.sign.domain.classroom.service;

import com.sign.domain.classroom.entity.Room;
import com.sign.domain.classroom.repository.RoomRepository;
import com.sign.domain.classroom.service.dto.RoomCreateForm;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.repository.MemberRepository;
import com.sign.domain.websocket.ChatEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService{

    private final MemberRepository memberRepository;
    private final RoomRepository classroomRepository;
    private final ChatEventListener chatEventListener;

    // roomId, sessionId, color
    @Override
    public Optional<Room> findRoomByRoomCode(String roomCode) {
        return classroomRepository.findByCode(roomCode);
    }

    @Override
    public List<Room> findRoomByRoomName(String roomName) {
        return classroomRepository.findByName(roomName);
    }

    @Override
    public Optional<Room> findRoomByRoomId(Long roomId) {
        Optional<Room> result = classroomRepository.findById(roomId);
        return result;
    }

    @Override
    public Set<Room> findJoiningRooms(Member member) {
        return member.getJoiningRooms();
    }


    @Override
    public List<Room> findHostingRooms(Member host) {
        return classroomRepository.findByHost(host);
    }

    @Override
    public Room createRoom(RoomCreateForm form, Member member) {
        //log.info("loginMember.getMember(): {}", loginMember.getMember());
        Member host = memberRepository.findById(member.getId()).get();
        Room classroom = new Room();
        classroom.setRoomCode(form.getRoomCode());
        classroom.setRoomName(form.getRoomName());
        classroom.setHost(host);
        Room created = classroomRepository.save(classroom);
        joinRoom(host, created);
//        joinRoom(loginMember.getMember(), classroom.getRoomCode());
        return classroom;
    }

    @Override
    public Room joinRoom(Member member, Room classroom) {
        Member memberToJoin = memberRepository.findById(member.getId()).get();
        memberToJoin.addJoiningRoom(classroom);
        log.info("member.getJoiningRooms: {}",memberToJoin.getJoiningRooms());
        memberRepository.save(memberToJoin);
        return classroom;
    }

    @Override
    public boolean checkJoined(Member member, Room classroom) {
        Member checkingMember = memberRepository.findById(member.getId()).get();
        if (findJoiningRooms(checkingMember).contains(classroom)) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteRoom(Room classroom) {
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
