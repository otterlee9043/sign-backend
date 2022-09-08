package com.sign.domain.classroom.service;

import com.sign.domain.classroom.entity.Room;
import com.sign.domain.classroom.service.dto.RoomCreateForm;
import com.sign.domain.member.entity.LoginMember;
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

    Room createRoom(RoomCreateForm classroom, LoginMember loginMember);

    Room joinRoom(Member member, Room classroom);

    boolean checkJoined(Member member, Room classroom);

    void deleteRoom(Room room);

    Map<Integer, String> getRoomStates(Integer roomId);

    Integer getMySeatPosition(Integer roomId, String username);
}
