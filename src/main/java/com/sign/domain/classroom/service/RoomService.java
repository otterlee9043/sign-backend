package com.sign.domain.classroom.service;

import com.sign.domain.classroom.controller.dto.RoomCreateRequest;
import com.sign.domain.classroom.controller.dto.RoomUpdateRequest;
import com.sign.domain.classroom.entity.Room;
import com.sign.domain.member.entity.Member;

import java.util.List;

public interface RoomService {

    void createRoom(RoomCreateRequest classroom, Member member);

    void updateRoom(Room room, RoomUpdateRequest request);

    void deleteRoom(Room room, Member member);

    Room joinRoom(Member member, Room classroom);

    void enterRoom(Room room, Member member);

    Room getRoom(Long roomId);

    Room findRoomByRoomCode(String roomCode);

    List<Room> findJoiningRooms(Member member);

    boolean doesRoomCodeExist(String roomCode);
}
