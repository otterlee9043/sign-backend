package com.sign.domain.classroom.repository;

import com.sign.domain.classroom.entity.Room;
import com.sign.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByName(String name);

    Optional<Room> findByCode(String code);

    List<Room> findByHost(Member host);

}
