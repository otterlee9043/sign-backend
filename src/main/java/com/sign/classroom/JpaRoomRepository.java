package com.sign.classroom;

import com.sign.member.Member;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JpaRoomRepository implements ClassroomRepository{

    private final EntityManager em;

    @Override
    public Classroom save(Classroom classroom) {
        em.persist(classroom);
        return classroom;
    }

    @Override
    public Optional<Classroom> findById(Long roomId) {
        Classroom classroom = em.find(Classroom.class, roomId);
        return Optional.ofNullable(classroom);
    }

    @Override
    public List<Classroom> findByName(String roomName) {
        List<Classroom> result = em.createQuery("select m from Classroom m where m.roomName =:name", Classroom.class)
                .setParameter("name", roomName)
                .getResultList();
        return result;
    }

    @Override
    public List<Classroom> findByHost(Member host) {
        List<Classroom> result = em.createQuery("select m from Classroom m where m.hostId =:hostId", Classroom.class)
                .setParameter("hostId", hostId)
                .getResultList();
        return result;
    }

    @Override
    public List<Classroom> findAll() {
        return em.createQuery("select m from Classroom m", Classroom.class).getResultList();
    }

    @Override
    public void delete(Classroom classroom) {
        em.remove(classroom);
    }
}
