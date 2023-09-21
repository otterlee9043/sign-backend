package com.sign.domain.classroom.repository;

import com.sign.domain.classroom.entity.Room;
import com.sign.domain.member.Role;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@DataJpaTest
class RoomRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoomRepository roomRepository;

    private Member getSavedMember() {
        Member member = Member.builder()
                .username("username")
                .email("user@email.com")
                .role(Role.USER)
                .build();
        return memberRepository.save(member);
    }

    private static Room getRoom(Member host, String code) {
        return Room.builder()
                .name("room")
                .host(host)
                .code(code)
                .capacity(10)
                .build();
    }

    @BeforeEach
    void cleanup() {
        roomRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Nested
    @DisplayName("findByName 메소드는")
    class DescribeFindByName {

        @Nested
        @DisplayName("존재하지 않는 방 이름이라면")
        class ContextWithNonExistentRoomName {

            @Test
            @DisplayName("빈 List를 반환한다.")
            void itReturnsEmptyList() {
                //given, when
                List<Room> room = roomRepository.findByName("room");

                //then
                assertThat(room).isEmpty();
            }

        }

        @Nested
        @DisplayName("올바른 방 이름이라면")
        class ContextWithValidRoomName {

            @Test
            @DisplayName("List<Room>을 반환한다.")
            void itReturnsRoomList() {
                //given
                Member host = getSavedMember();

                Room savedRoom1 = roomRepository.save(getRoom(host, "code1"));
                Room savedRoom2 = roomRepository.save(getRoom(host, "code2"));

                //when
                List<Room> result = roomRepository.findByName(savedRoom1.getName());

                //then
                assertThat(result.size()).isEqualTo(2);
                assertThat(result).contains(savedRoom1);
                assertThat(result).contains(savedRoom2);
            }

        }
    }

    @Nested
    @DisplayName("findByCode 메소드는")
    class DescribeFindByCode {

        @Nested
        @DisplayName("존재하지 않는 방 코드라면")
        class ContextWithNonExistentRoomName {

            @Test
            @DisplayName("Optional.empty()를 반환한다.")
            void itReturnsEmptyList() {
                //given, when
                Optional<Room> room = roomRepository.findByCode("room");

                //then
                assertThat(room).isEmpty();
            }

        }

        @Nested
        @DisplayName("올바른 방 코드라면")
        class ContextWithValidRoomName {

            @Test
            @DisplayName("Room을 포함한 Optional을 반환한다.")
            void itReturnsOptionalRoom() {
                //given
                Member host = getSavedMember();
                Room room = roomRepository.save(getRoom(host, "code1"));

                //when
                Optional<Room> result = roomRepository.findByCode(room.getCode());

                //then
                assertThat(result.get()).isEqualTo(room);
            }
        }

        @Nested
        @DisplayName("findByHost 메소드는")
        class DescribeFindByHost {

            @Nested
            @DisplayName("올바른 host라면")
            class ContextWithValidHost {

                @Test
                @DisplayName("List<Room>을 반환한다.")
                void itReturnsRoomList() {
                    //given
                    Member host = getSavedMember();
                    Room savedRoom1 = roomRepository.save(getRoom(host, "code1"));
                    Room savedRoom2 = roomRepository.save(getRoom(host, "code2"));

                    //when
                    List<Room> result = roomRepository.findByHost(host);

                    //then
                    assertThat(result.size()).isEqualTo(2);
                    assertThat(result).contains(savedRoom1);
                    assertThat(result).contains(savedRoom2);
                }

            }
        }
    }

}