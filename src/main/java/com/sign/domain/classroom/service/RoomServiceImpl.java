package com.sign.domain.classroom.service;

import com.sign.domain.classroom.controller.dto.RoomUpdateRequest;
import com.sign.domain.classroom.entity.Joins;
import com.sign.domain.classroom.entity.Room;
import com.sign.domain.classroom.repository.RoomRepository;
import com.sign.domain.classroom.controller.dto.RoomCreateRequest;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.repository.MemberRepository;
import com.sign.domain.websocket.ChatEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService{

    private final MemberRepository memberRepository;
    private final RoomRepository classroomRepository;
    private final ChatEventListener chatEventListener;

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
        return member.getJoins().stream()
                .map(Joins::getRoom)
                .collect(Collectors.toSet());
    }


    @Override
    public List<Room> findHostingRooms(Member host) {
        return classroomRepository.findByHost(host);
    }

    @Override
    public Room createRoom(RoomCreateRequest request, Member host) {
        Room classroom = Room.builder()
                        .name(request.getRoomName())
                                .code(request.getRoomCode())
                                        .host(host)
                                                .build();

        Room created = classroomRepository.save(classroom);
        log.info("createdRoom: {}", created);
        return joinRoom(host, created);
    }

    @Override
    public Room joinRoom(Member member, Room classroom) {
        Member memberToJoin = memberRepository.findById(member.getId()).get();
        Joins joins = Joins.builder()
                .member(memberToJoin)
                .room(classroom)
                .build();
        classroomRepository.save(joins);
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
    public void updateRoom(Room room, RoomUpdateRequest request) {
        room.updateRoom(request.getRoomName());
    }

    @Override
    public void deleteRoom(Room classroom, Member member) {
        log.info("classroom.getHost(): {}", classroom.getHost());
        log.info("member: {}", member);
        if (classroom.getHost().getId().equals(member.getId())) {
            classroomRepository.delete(classroom);
        } else {
            throw new AccessDeniedException("방을 수정할 권한이 없습니다.");
        }
    }


    @Override
    public Map<Integer, String> getRoomStates(Integer roomId) {
        return chatEventListener.getRoomStatesByRoomId(roomId);
    }

    @Override
    public Integer getMySeatPosition(Integer roomId, String sessionId) {
        return chatEventListener.getMySeatPosition(roomId, sessionId);
    }

    public boolean doesRoomCodeExist(String roomCode) {
        return classroomRepository.findByCode(roomCode).isPresent();
    }
}
