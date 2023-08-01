package com.sign.domain.member.controller;

import com.sign.domain.classroom.controller.dto.RoomResponse;
import com.sign.domain.classroom.entity.Room;
import com.sign.domain.classroom.service.RoomService;
import com.sign.domain.member.controller.dto.MemberProfile;
import com.sign.domain.member.controller.dto.SignupRequest;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.service.MemberService;
import com.sign.global.exception.DataDuplicateException;
import com.sign.global.security.authentication.LoginMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MemberController {
    private final MemberService memberService;
    private final RoomService classroomService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/members")
    public void signup(@RequestBody @Valid SignupRequest request) throws Exception {
        memberService.join(request);
    }


    @GetMapping("/member")
    public MemberProfile getMyProfile(@AuthenticationPrincipal LoginMember loginMember) {
        return MemberProfile.from(loginMember.getMember());
    }

    @DeleteMapping("/member/{memberId}")
    public void unregister(@PathVariable Long memberId, @AuthenticationPrincipal LoginMember loginMember) {
        Member member = loginMember.getMember();
        if (memberId.equals(member.getId())) {
            memberService.deleteMember(member);
        }
    }

    @GetMapping("/member/{memberId}")
    public MemberProfile getProfile(@PathVariable Long memberId) {
        Member member = memberService.findMember(memberId);
        return MemberProfile.from(member);
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/member/{memberId}/classrooms")
    public List<RoomResponse> getJoiningRooms(@PathVariable Long memberId) {
        Member member = memberService.findMember(memberId);
        List<Room> joiningRooms = classroomService.findJoiningRooms(member);

        return joiningRooms.stream()
                .map(RoomResponse::from)
                .collect(Collectors.toList());
    }


    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/member/{memberId}/classroom/{roomId}")
    public void join(@PathVariable Long memberId, @PathVariable Long roomId) {
        Room room = classroomService.findRoomByRoomId(roomId);
        Member member = memberService.findMember(memberId);
        classroomService.joinRoom(member, room);
    }


    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/member/{memberId}/classroom/{roomId}")
    public void enter(@PathVariable Long memberId, @PathVariable Long roomId) {
        Room room = classroomService.findRoomByRoomId(roomId);
        Member member = memberService.findMember(memberId);
        classroomService.enterRoom(room, member);
    }


    @GetMapping("/members/email/{email}/duplication")
    public void checkEmail(@PathVariable String email) {
        if (memberService.doesEmailExist(email)) {
            throw new DataDuplicateException("이미 사용 중인 이메일입니다.");
        }
    }
}

