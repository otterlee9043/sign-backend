package com.sign.domain.classroom.controller;

import com.sign.domain.classroom.service.dto.RoomCreateForm;
import com.sign.domain.classroom.service.RoomService;
import com.sign.domain.classroom.entity.Room;
import com.sign.security.LoginMember;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService classroomService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/classrooms")
    public String create(@Validated @RequestBody RoomCreateForm form, BindingResult bindingResult,
                         @AuthenticationPrincipal LoginMember loginMember){
        log.info("form={}", form);
        Room room = classroomService.createRoom(form, loginMember);
        log.info("Right before return response");
        return "classroom successfully created";
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/classrooms")
    public Set<Room> joiningRooms(@AuthenticationPrincipal LoginMember loginMember){
        Member member = memberService.findMember(loginMember.getMember().getId()).get();
        Set<Room> joiningRooms = classroomService.findJoiningRooms(member);
        return joiningRooms;
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/classroom/{roomId}/join")
    public String join(@PathVariable Long roomId, @AuthenticationPrincipal LoginMember loginMember){
        Optional<Room> classroomOptional = classroomService.findRoomByRoomId(roomId);
        if (classroomOptional.isEmpty()){
            return "There is no room with id of " + roomId;
        } else {
            classroomService.joinRoom(loginMember.getMember(), classroomOptional.get());
            return "successfully joined!";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/classroom/{roomId}")
    public String enter(@PathVariable Long roomId, @AuthenticationPrincipal LoginMember loginMember){
        Optional<Room> classroomOptional = classroomService.findRoomByRoomId(roomId);
        if (classroomOptional.isEmpty()){
            return "There is no room with id of " + roomId;
        } else {
            Room classroom = classroomOptional.get();
            if (classroomService.checkJoined(loginMember.getMember(),  classroom)){
                return "Enter to the classroom!";
            } else {
                return "You are not joined in this room";
            }
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/classrooms/{roomCode}")
    public String findRoomByCode(@PathVariable String roomCode) {
        Optional<Room> room = classroomService.findRoomByRoomCode(roomCode);
        log.info("findRoomByCode");
        if (room.isEmpty()) {
            return "no room";
        } else {
            log.info("room.get().getId().toString():", room.get().getId().toString());
            return room.get().getId().toString();
        }
    }
}
