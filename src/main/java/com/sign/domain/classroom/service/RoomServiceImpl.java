package com.sign.domain.classroom.service;

import com.sign.domain.classroom.controller.dto.RoomCreateRequest;
import com.sign.domain.classroom.controller.dto.RoomUpdateRequest;
import com.sign.domain.classroom.entity.Joins;
import com.sign.domain.classroom.entity.Room;
import com.sign.domain.classroom.exception.RoomCapacityExceededException;
import com.sign.domain.classroom.repository.JoinsRepository;
import com.sign.domain.classroom.repository.RoomRepository;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.repository.MemberRepository;
import com.sign.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final MemberRepository memberRepository;

    private final RoomRepository classroomRepository;

    private final JoinsRepository joinsRepository;

    @Override
    public void createRoom(RoomCreateRequest request, Member host) {
        Room classroom = Room.builder()
                .name(request.getName())
                .host(host)
                .code(request.getCode())
                .capacity(request.getCapacity())
                .build();
        classroomRepository.save(classroom);
        Joins joins = new Joins(host, classroom);
        joinsRepository.save(joins);
    }

    @Override
    public void updateRoom(Room room, RoomUpdateRequest request) {
        room.updateRoom(request.getRoomName());
    }

    @Override
    public void deleteRoom(Room classroom, Member member) {
        if (classroom.getHost().getId().equals(member.getId())) {
            classroomRepository.delete(classroom);
        } else {
            throw new AccessDeniedException("방을 수정할 권한이 없습니다.");
        }
    }

    @Override
    public Room joinRoom(Member member, Room classroom) {
        Member memberToJoin = memberRepository.findById(member.getId())
                .orElseThrow(() -> new NotFoundException("Member doesn't exist."));
        if (classroom.getJoined().size() >= classroom.getCapacity())
            throw new RoomCapacityExceededException();
        joinsRepository.save(new Joins(memberToJoin, classroom));
        return classroom;
    }

    @Override
    public void enterRoom(Room room, Member member) {
        Joins joins = joinsRepository.findByRoomAndMember(room, member)
                .orElseThrow(() -> new NotFoundException("Member did not join this room"));
        joins.updateEnteredTime();
    }

    @Override
    public Room getRoom(Long roomId) {
        return classroomRepository.findById(roomId).orElseThrow(() ->
                new NotFoundException("해당 ID의 방을 찾을 수 없습니다."));
    }

    @Override
    public List<Room> getJoiningRooms(Member member) {
        List<Joins> joins = member.getJoins();
        return joins.stream()
                .map(Joins::getRoom)
                .collect(Collectors.toList());
    }

    @Override
    public Room findRoomByRoomCode(String roomCode) {
        return classroomRepository.findByCode(roomCode).orElseThrow(() ->
                new NotFoundException("해당 입장 코드의 방을 찾을 수 없습니다."));
    }

    @Override
    public boolean doesRoomCodeExist(String roomCode) {
        return classroomRepository.findByCode(roomCode).isPresent();
    }

}
