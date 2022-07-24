package com.sign.classroom;

import java.util.List;

public interface ClassroomService {
    List<Classroom> findJoiningRooms(Long memberId);

    List<Classroom> findHostingRooms(Long memberId);

    Classroom createRoom(Classroom classroom);

    void deleteRoom(Classroom room);

    Classroom joinRoom(String roomCode);
}
