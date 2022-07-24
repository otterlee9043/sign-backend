package com.sign.classroom;

import com.sign.member.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ClassroomServiceImpl implements ClassroomService{

    private final MemberRepository memberRepository;
    private final ClassroomRepository classroomRepository;

    @Override
    public List<Classroom> findJoiningRooms(Long memberId) {
//        List<Classroom> rooms = new List<>();
//        for(Classroom classroom : classroomRepository.findAll()){
//            Long roomId = classroom.getId();
//
//            if(entry.getValue().equals(34)) { // 값이 null이면 NullPointerException 예외 발생
//                findKey = entry.getKey();
//                break;
//            }
//        }

        return null;
    }

    @Override
    public List<Classroom> findHostingRooms(Long memberId) {
        return null;
    }

    @Override
    public Classroom createRoom(Classroom classroom) {
        return null;
    }

    @Override
    public void deleteRoom(Classroom room) {

    }

    @Override
    public Classroom joinRoom(String roomCode) {
        return null;
    }


}
