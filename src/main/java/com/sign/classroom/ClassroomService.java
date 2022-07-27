package com.sign.classroom;

import com.sign.member.Member;

import java.util.List;
import java.util.Optional;

public interface ClassroomService {

    Optional<Classroom> findRoomByRoomCode(String roomCode);

    List<Classroom> findRoomByRoomName(String roomName);

    List<Classroom> findJoiningRooms(Member member);


    List<Classroom> findHostingRooms(Member host);

    Classroom createRoom(Classroom classroom);

    Classroom joinRoom(Member member, String roomCode);

    void deleteRoom(Classroom room);

}
