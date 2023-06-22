package com.sign.domain.classroom.controller;

import com.sign.domain.classroom.controller.dto.RoomResponse;
import com.sign.domain.classroom.controller.dto.RoomCreateRequest;
import com.sign.domain.classroom.controller.dto.RoomMapper;
import com.sign.domain.classroom.controller.dto.RoomUpdateRequest;
import com.sign.domain.classroom.service.RoomService;
import com.sign.domain.classroom.entity.Room;
import com.sign.global.security.authentication.LoginMember;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService classroomService;
    private final MemberService memberService;

    @PostMapping("/api/classrooms")
    public ResponseEntity create(@Validated @RequestBody RoomCreateRequest request,
                                 @AuthenticationPrincipal LoginMember loginMember){
        classroomService.createRoom(request, loginMember.getMember());
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }


    @GetMapping("/api/classrooms")
    public Set<RoomResponse> joiningRooms(@AuthenticationPrincipal LoginMember loginMember){
        log.info("/api/classrooms");
        Member member = memberService.findMember(loginMember.getMember().getId()).get();
        Set<Room> joiningRooms = classroomService.findJoiningRooms(member);
        return joiningRooms.stream()
                .map(room -> RoomResponse.builder()
                        .id(room.getId())
                        .roomName(room.getName())
                        .hostUsername(room.getHost().getUsername())
                        .hostEmail(room.getHost().getEmail())
                        .capacity(room.getCapacity())
                        .build())
                .collect(Collectors.toSet());
    }


    @PostMapping("/api/classroom/{roomId}/join")
    public ResponseEntity join(@PathVariable Long roomId, @AuthenticationPrincipal LoginMember loginMember){
        Room room = classroomService.findRoomByRoomId(roomId);
        classroomService.joinRoom(loginMember.getMember(), room);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }


    @GetMapping("/api/classroom/{roomId}")
    public ResponseEntity enter(@PathVariable Long roomId, @AuthenticationPrincipal LoginMember loginMember){
        Room room = classroomService.findRoomByRoomId(roomId);
        RoomResponse roomResponse = RoomResponse.builder()
                .id(room.getId())
                .roomName(room.getName())
                .capacity(room.getCapacity())
                .hostEmail(room.getHost().getEmail())
                .hostUsername(room.getHost().getUsername())
                .build();

        return new ResponseEntity<>(roomResponse, HttpStatus.OK);
    }

    @PutMapping("/api/classroom/{roomId}")
    public void updateRoom(@PathVariable Long roomId, @RequestBody RoomUpdateRequest request,
                                         @AuthenticationPrincipal LoginMember loginMember) {
        log.info("updateRoom | new room name: {}", request.getRoomName());
        Room room = classroomService.findRoomByRoomId(roomId);

        if (!room.getHost().getId().equals(loginMember.getMember().getId())){
            log.info("방 주인이 아님");
            throw new AccessDeniedException("방을 수정할 권한이 없습니다.");
        }
        classroomService.updateRoom(room, request);
    }

    @DeleteMapping("/api/classroom/{roomId}")
    public void deleteRoom(@PathVariable Long roomId, @AuthenticationPrincipal LoginMember loginMember){
        Room room = classroomService.findRoomByRoomId(roomId);
        classroomService.deleteRoom(room, loginMember.getMember());

    }


    @GetMapping("/api/classrooms/byCode/{roomCode}")
    public ResponseEntity findRoomByCode(@PathVariable String roomCode) {
        Room room = classroomService.findRoomByRoomCode(roomCode);
        return new ResponseEntity<>(RoomMapper.toRoomInfo(room), HttpStatus.OK);
    }

    @GetMapping("/api/classroom/roomCode/{roomCode}/exists")
    public ResponseEntity checkRoomCodeExistence(@PathVariable String roomCode) {
        Map<String, Object> result = new HashMap<>();
        result.put("duplicate", classroomService.doesRoomCodeExist(roomCode));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
