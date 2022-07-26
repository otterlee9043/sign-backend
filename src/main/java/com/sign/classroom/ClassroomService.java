package com.sign.classroom;

import com.sign.member.Member;

import java.util.List;

public interface ClassroomService {
    List<Classroom> findJoiningRooms(Long memberId);

    List<Classroom> findHostingRooms(Member host);

    Classroom createRoom(Classroom classroom);

    void deleteRoom(Classroom room);

    Classroom joinRoom(String roomCode);
}
