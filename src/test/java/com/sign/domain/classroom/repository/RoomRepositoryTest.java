package com.sign.domain.classroom.repository;

import com.sign.domain.classroom.entity.Room;
import com.sign.domain.member.Role;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

    private Member getMember() {
        Member member = Member.builder()
                .username("username")
                .email("user@email.com")
                .role(Role.USER)
                .build();
        return memberRepository.save(member);
    }

    private Room getRoom(String roomName, String roomCode, Member host) {
        Room room = Room.builder()
                .name(roomName)
                .host(host)
                .code(roomCode)
                .capacity(10)
                .build();
        return roomRepository.save(room);
    }


    @Nested
    @DisplayName("findByUsername 메소드는")
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
                Member host = getMember();
                String roomName = "room";
                Room room1 = getRoom(roomName, "code1", host);
                Room room2 = getRoom(roomName, "code2", host);

                //when
                List<Room> result = roomRepository.findByName(roomName);

                //then
                assertThat(result.size()).isEqualTo(2);
                assertThat(result).contains(room1);
                assertThat(result).contains(room2);
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
        @DisplayName("올바른 방 이름이라면")
        class ContextWithValidRoomName {

            @Test
            @DisplayName("Room을 포함한 Optional을 반환한다.")
            void itReturnsOptionalRoom() {
                //given
                Member host = getMember();
                String roomCode = "code";
                Room room = getRoom("name", roomCode, host);

                //when
                Optional<Room> result = roomRepository.findByCode(roomCode);

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
                @DisplayName("Optional.empty()를 반환한다.")
                void itReturnsRoomList() {
                    //given
                    Member host = getMember();
                    Room room1 = getRoom("name1", "code1", host);
                    Room room2 = getRoom("name2", "code2", host);
                    //when
                    List<Room> result = roomRepository.findByHost(host);

                    //then
                    assertThat(result.size()).isEqualTo(2);
                    assertThat(result).contains(room1);
                    assertThat(result).contains(room2);
                }

            }
        }
    }

}