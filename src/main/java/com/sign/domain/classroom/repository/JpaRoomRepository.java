package com.sign.domain.classroom.repository;

import com.sign.domain.classroom.entity.Joins;
import com.sign.domain.classroom.entity.Room;
import com.sign.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Transactional
public class JpaRoomRepository implements RoomRepository{

    private final EntityManager em;

    @Override
    public Room save(Room classroom) {
        em.persist(classroom);
        return classroom;
    }

    @Override
    public Joins save(Joins joins) {
        em.persist(joins);
        return joins;
    }

    @Override
    public Optional<Room> findById(Long roomId) {
        Room classroom = em.find(Room.class, roomId);
        return Optional.ofNullable(classroom);
    }

    @Override
    public List<Room> findByName(String name) {
        List<Room> result = em.createQuery("select m from Room m where m.name =:name", Room.class)
                .setParameter("name", name)
                .getResultList();
        return result;
    }

    @Override
    public Optional<Room> findByCode(String code) {
        List<Room> result = em.createQuery("select m from Room m where m.code =:code", Room.class)
                .setParameter("code", code)
                .getResultList();
        return result.stream().findAny();
    }

    @Override
    public List<Room> findByHost(Member host) {
        Long hostId = host.getId();
        List<Room> result = em.createQuery("select m from Room m where m.hostId =:hostId", Room.class)
                .setParameter("hostId", hostId)
                .getResultList();
        return result;
    }

    @Override
    public List<Room> findAll() {
        return em.createQuery("select m from Room m", Room.class).getResultList();
    }

    @Override
    public void delete(Room classroom) {
        em.remove(classroom);
    }

    @Override
    public boolean checkAttached(Room classroom){
        return em.contains(classroom);
    }

}
