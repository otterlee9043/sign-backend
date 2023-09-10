package com.sign.domain.classroom.controller;

import com.sign.domain.classroom.controller.dto.RoomCreateRequest;
import com.sign.domain.classroom.controller.dto.RoomResponse;
import com.sign.domain.classroom.controller.dto.RoomUpdateRequest;
import com.sign.domain.classroom.entity.Room;
import com.sign.domain.classroom.service.RoomService;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.service.MemberService;
import com.sign.global.exception.DataDuplicateException;
import com.sign.global.security.authentication.LoginMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RoomController {

    private final RoomService classroomService;

    private final MemberService memberService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/classrooms")
    public void create(@Validated @RequestBody RoomCreateRequest request,
                       @AuthenticationPrincipal LoginMember loginMember) {
        log.info("validated");
        classroomService.createRoom(request, loginMember.getMember());
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/classrooms")
    public RoomResponse findRoomByCode(@RequestParam String code) {
        Room room = classroomService.findRoomByRoomCode(code);
        return RoomResponse.from(room);
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/classroom/{id}")
    public RoomResponse getRoom(@PathVariable Long id) {
        Room room = classroomService.getRoom(id);
        return RoomResponse.from(room);
    }


    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/classroom/{id}")
    public void updateRoom(@PathVariable Long id,
                           @RequestBody RoomUpdateRequest request,
                           @AuthenticationPrincipal LoginMember loginMember) {
        Room room = classroomService.getRoom(id);
        memberService.getVerifiedMember(room.getHost().getId(), loginMember);
        classroomService.updateRoom(room, request);
    }


    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/classroom/{id}")
    public void deleteRoom(@PathVariable Long id,
                           @AuthenticationPrincipal LoginMember loginMember) {
        Room room = classroomService.getRoom(id);
        Member member = memberService.getVerifiedMember(room.getHost().getId(), loginMember);
        classroomService.deleteRoom(room, member);
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/classrooms/code/{code}/duplication")
    public void checkRoomCodeExistence(@PathVariable String code) {
        if (classroomService.doesRoomCodeExist(code)) {
            throw new DataDuplicateException("이미 사용 중인 코드입니다.");
        }
    }
}
