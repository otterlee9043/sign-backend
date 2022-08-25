package com.sign.domain.classroom;

import com.sign.domain.member.LoginMember;
import com.sign.domain.member.Member;
import com.sign.domain.member.MemberSecurityService;
import com.sign.domain.member.MemberService;
import com.sign.domain.websocket.SeatInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/classrooms")
    public String create(@Validated @RequestBody ClassroomCreateForm form, BindingResult bindingResult,
                         @AuthenticationPrincipal LoginMember loginMember){
        log.info("form={}", form);
        Classroom room = classroomService.createRoom(form, loginMember);
        log.info("Right before return response");
        return "classroom successfully created";
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/classrooms")
    public Set<Classroom> joiningRooms(@AuthenticationPrincipal LoginMember loginMember){
        Member member = memberService.findMember(loginMember.getMember().getId()).get();
        Set<Classroom> joiningRooms = classroomService.findJoiningRooms(member);
        return joiningRooms;
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/classroom/{roomId}/join")
    public String join(@PathVariable Long roomId, @AuthenticationPrincipal LoginMember loginMember){
        Optional<Classroom> classroomOptional = classroomService.findRoomByRoomId(roomId);
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
        Optional<Classroom> classroomOptional = classroomService.findRoomByRoomId(roomId);
        if (classroomOptional.isEmpty()){
            return "There is no room with id of " + roomId;
        } else {
            Classroom classroom = classroomOptional.get();
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
        Optional<Classroom> room = classroomService.findRoomByRoomCode(roomCode);
        log.info("findRoomByCode");
        if (room.isEmpty()) {
            return "no room";
        } else {
            log.info("room.get().getId().toString():", room.get().getId().toString());
            return room.get().getId().toString();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/classroom/{roomId}/states")
    public Map<Integer, String> getCurrentRoomStates(@PathVariable String roomId) {
        return classroomService.getRoomStates(roomId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/classroom/{roomId}/mySeat")
    public String getMyPosition(@PathVariable String roomId, @AuthenticationPrincipal LoginMember loginMember) {
        log.info("classroomService.getMySeatPosition(roomId, loginMember.getUsername()).toString()={}", classroomService.getMySeatPosition(roomId, loginMember.getUsername()).toString());
        return classroomService.getMySeatPosition(roomId, loginMember.getUsername()).toString();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/classroom/{roomId}/seatInfo")
    public SeatInfo getClassroomInfo(@PathVariable String roomId, @AuthenticationPrincipal LoginMember loginMember){
        SeatInfo seatInfo = new SeatInfo();
        seatInfo.setClassRoomStates(classroomService.getRoomStates(roomId));
        seatInfo.setSeatNum(classroomService.getMySeatPosition(roomId, loginMember.getUsername()));
        return seatInfo;
    }
}
