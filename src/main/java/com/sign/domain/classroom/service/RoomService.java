package com.sign.domain.classroom.service;

import com.sign.domain.classroom.controller.dto.RoomUpdateRequest;
import com.sign.domain.classroom.entity.Room;
import com.sign.domain.classroom.controller.dto.RoomCreateRequest;
import com.sign.domain.member.entity.Member;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface RoomService {

    Optional<Room> findRoomByRoomCode(String roomCode);

    List<Room> findRoomByRoomName(String roomName);

    Optional<Room> findRoomByRoomId(Long roomId);

    Set<Room> findJoiningRooms(Member member);

    List<Room> findHostingRooms(Member host);

    Room createRoom(RoomCreateRequest classroom, Member member);

    Room joinRoom(Member member, Room classroom);

    boolean checkJoined(Member member, Room classroom);

    void updateRoom(Room room, RoomUpdateRequest request);
    void deleteRoom(Room room, Member member);

    Map<Integer, String> getRoomStates(Integer roomId);

    Integer getMySeatPosition(Integer roomId, String username);

    boolean doesRoomCodeExist(String roomCode);
}
