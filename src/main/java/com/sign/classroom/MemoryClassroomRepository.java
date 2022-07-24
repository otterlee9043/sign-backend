package com.sign.classroom;

import com.sign.member.Member;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MemoryClassroomRepository implements ClassroomRepository{

    private static Map<Long, Classroom> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Classroom save(Classroom room) {
        room.setId(++sequence);
        store.put(room.getId(), room);
        return room;
    }

    @Override
    public Classroom findById(Long roomId) {
        return store.get(roomId);
    }

    @Override
    public Classroom findByName(String roomName) {
        return null;
    }

    @Override
    public void delete(Long roomId) {

    }

    @Override
    public List<Classroom> findAll(){
        return (List<Classroom>) store.values();
    }
}
