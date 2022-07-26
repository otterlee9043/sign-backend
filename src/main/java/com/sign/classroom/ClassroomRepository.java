package com.sign.classroom;

import com.sign.member.Member;

import java.util.List;
import java.util.Optional;

public interface ClassroomRepository {

    Classroom save(Classroom classroom);

    Optional<Classroom> findById(Long roomId);

    List<Classroom> findByName(String roomName);

    List<Classroom> findByHost(Member host);

    List<Classroom> findAll();

    void delete(Classroom classroom);
}
