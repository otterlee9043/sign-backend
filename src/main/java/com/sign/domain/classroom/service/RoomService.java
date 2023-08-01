package com.sign.domain.classroom.service;

import com.sign.domain.classroom.controller.dto.RoomCreateRequest;
import com.sign.domain.classroom.controller.dto.RoomUpdateRequest;
import com.sign.domain.classroom.entity.Room;
import com.sign.domain.member.entity.Member;

import java.util.List;

public interface RoomService {

    Room findRoomByRoomCode(String roomCode);

    List<Room> findRoomByRoomName(String roomName);

    Room findRoomByRoomId(Long roomId);

    List<Room> findJoiningRooms(Member member);

    List<Room> findHostingRooms(Member host);

    void createRoom(RoomCreateRequest classroom, Member member);

    Room joinRoom(Member member, Room classroom);

    boolean checkJoined(Member member, Room classroom);

    void enterRoom(Room room, Member member);

    void updateRoom(Room room, RoomUpdateRequest request);

    void deleteRoom(Room room, Member member);

    Integer getRoomCapacity(Long roomId);

    boolean doesRoomCodeExist(String roomCode);
}
