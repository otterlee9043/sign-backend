package com.sign.classroom;

import com.sign.member.Member;
import com.sign.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ClassroomServiceImpl implements ClassroomService{

    private final MemberRepository memberRepository;
    private final ClassroomRepository classroomRepository;

    @Override
    public List<Classroom> findJoiningRooms(Long memberId) {
        /**
         * TODO Registration table 만들고 구현할 것
         */
        return null;
    }

    @Override
    public List<Classroom> findHostingRooms(Member host) {
        return classroomRepository.findByHost(host);
    }

    @Override
    public Classroom createRoom(Classroom classroom) {
        return classroomRepository.save(classroom);
    }

    @Override
    public void deleteRoom(Classroom classroom) {
        classroomRepository.delete(classroom);
    }

    @Override
    public Classroom joinRoom(String roomCode) {
        return null;
    }


}
