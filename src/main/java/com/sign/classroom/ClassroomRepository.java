package com.sign.classroom;

import com.sign.member.Member;

import java.util.List;

public interface ClassroomRepository {

    Classroom save(Classroom member);

    Classroom findById(Long roomId);

    Classroom findByName(String roomName);

    List<Classroom> findAll();

    void delete(Long roomId);
}
