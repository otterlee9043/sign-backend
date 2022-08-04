package com.sign.domain.classroom;

import com.sign.member.Member;
import com.sign.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ClassroomServiceImpl implements ClassroomService{

    private final MemberRepository memberRepository;
    private final ClassroomRepository classroomRepository;


    @Override
    public Optional<Classroom> findRoomByRoomCode(String roomCode) {
        return classroomRepository.findByCode(roomCode);
    }

    @Override
    public List<Classroom> findRoomByRoomName(String roomName) {
        return classroomRepository.findByName(roomName);
    }

    @Override
    public Set<Classroom> findJoiningRooms(Member member) {
        return member.getJoiningRooms();
    }


    @Override
    public List<Classroom> findHostingRooms(Member host) {
        return classroomRepository.findByHost(host);
    }

    @Override
    public Classroom createRoom(Classroom classroom) {
        classroomRepository.save(classroom);
        joinRoom(classroom.getHost(), classroom.getRoomCode());
        return classroom;
    }

    @Override
    public Classroom joinRoom(Member member, String roomCode) {
        Optional<Classroom> result = classroomRepository.findByCode(roomCode);
        if (result.isPresent()){
            Classroom classroom = result.get();
            member.addJoiningRoom(classroom);
            memberRepository.save(member);
        }

        return result.orElseThrow();
    }

    @Override
    public void deleteRoom(Classroom classroom) {
        classroomRepository.delete(classroom);
    }

}
