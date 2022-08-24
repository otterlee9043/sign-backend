package com.sign.domain.classroom;

import com.sign.domain.member.LoginMember;
import com.sign.domain.member.Member;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ClassroomService {

    Optional<Classroom> findRoomByRoomCode(String roomCode);

    List<Classroom> findRoomByRoomName(String roomName);

    Optional<Classroom> findRoomByRoomId(Long roomId);

    Set<Classroom> findJoiningRooms(Member member);

    List<Classroom> findHostingRooms(Member host);

    Classroom createRoom(ClassroomCreateForm classroom, LoginMember loginMember);

    Classroom joinRoom(Member member, Classroom classroom);

    boolean checkJoined(Member member, Classroom classroom);

    void deleteRoom(Classroom room);

    Map<Integer, String> getRoomStates(String roomId);

    Integer getMySeatPosition(String roomId);
}
