package com.sign.domain.classroom.repository;

import com.sign.domain.classroom.entity.Joins;
import com.sign.domain.classroom.entity.JoinsId;
import com.sign.domain.classroom.entity.Room;
import com.sign.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JoinsRepository extends JpaRepository<Joins, JoinsId> {
    Optional<Joins> findByRoomAndMember(Room room, Member member);
}
