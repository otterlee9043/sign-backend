package com.sign.domain.classroom.controller;

import com.sign.domain.classroom.controller.dto.RoomCreateRequest;
import com.sign.domain.classroom.controller.dto.RoomResponse;
import com.sign.domain.classroom.controller.dto.RoomUpdateRequest;
import com.sign.domain.classroom.entity.Room;
import com.sign.domain.classroom.service.RoomService;
import com.sign.global.exception.DataDuplicateException;
import com.sign.global.security.authentication.LoginMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RoomController {

    private final RoomService classroomService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/classrooms")
    public void create(@Validated @RequestBody RoomCreateRequest request,
                       @AuthenticationPrincipal LoginMember loginMember) {
        classroomService.createRoom(request, loginMember.getMember());
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/classrooms")
    public RoomResponse findRoomByCode(@RequestParam String code) {
        Room room = classroomService.findRoomByRoomCode(code);
        return RoomResponse.from(room);
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/classroom/{roomId}")
    public RoomResponse enter(@PathVariable Long roomId) {
        Room room = classroomService.findRoomByRoomId(roomId);
        return RoomResponse.from(room);
    }


    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/classroom/{roomId}")
    public void updateRoom(@PathVariable Long roomId, @RequestBody RoomUpdateRequest request,
                           @AuthenticationPrincipal LoginMember loginMember) {
        Room room = classroomService.findRoomByRoomId(roomId);

        if (!room.getHost().getId().equals(loginMember.getMember().getId())) {
            throw new AccessDeniedException("방을 수정할 권한이 없습니다.");
        }
        classroomService.updateRoom(room, request);
    }


    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/classroom/{roomId}")
    public void deleteRoom(@PathVariable Long roomId, @AuthenticationPrincipal LoginMember loginMember) {
        Room room = classroomService.findRoomByRoomId(roomId);
        classroomService.deleteRoom(room, loginMember.getMember());
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/classrooms/code/{code}/duplication")
    public void checkRoomCodeExistence(@PathVariable String code) {
        if (classroomService.doesRoomCodeExist(code)) {
            throw new DataDuplicateException("이미 사용 중인 코드입니다.");
        }
    }
}
