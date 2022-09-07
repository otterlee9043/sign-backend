package com.sign.classroom;

import com.sign.domain.classroom.entity.Classroom;
import com.sign.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

//@SpringBootTest
@DataJpaTest
//@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
//@ExtendWith(SpringExtension.class)
class JpaRoomRepositoryTest {
//    @Autowired MemberRepository memberRepository;
//    @Autowired ClassroomRepository classroomRepository;
    @Autowired
    TestEntityManager testEntityManager;
    @Test
    @Rollback(false)
    void save() {
        Member member = new Member();
        member.setUsername("goo");
//        memberRepository.save(member);
        testEntityManager.persistAndFlush(member);
        Classroom classroom = new Classroom();
        classroom.setRoomCode("code0");
        classroom.setHost(member);
        classroom.setRoomName("first room");
//        classroomRepository.save(classroom);
        testEntityManager.persistAndFlush(classroom);
//        testEntityManager.flush();
    }

    @Test
    void findById() {
    }

    @Test
    void findByName() {
    }

    @Test
    void findByHost() {
    }

    @Test
    void findAll() {
    }

    @Test
    void delete() {
    }
}