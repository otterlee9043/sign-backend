package com.sign.domain.classroom.service;

import com.sign.domain.classroom.controller.dto.RoomCreateRequest;
import com.sign.domain.classroom.entity.Joins;
import com.sign.domain.classroom.entity.Room;
import com.sign.domain.classroom.exception.RoomCapacityExceededException;
import com.sign.domain.classroom.repository.JoinsRepository;
import com.sign.domain.classroom.repository.RoomRepository;
import com.sign.domain.member.Role;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.repository.MemberRepository;
import com.sign.global.exception.NotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private JoinsRepository joinsRepository;

    @InjectMocks
    RoomServiceImpl roomService;

    private static final Member host = Member.builder()
            .username("username0")
            .email("user0@email.com")
            .role(Role.USER)
            .build();

    private static final Member member = Member.builder()
            .username("username1")
            .email("user1@email.com")
            .role(Role.USER)
            .build();

    private static final Member member2 = Member.builder()
            .username("username2")
            .email("user2@email.com")
            .role(Role.USER)
            .build();

    private static final Room room = Room.builder()
            .name("roomName")
            .host(host)
            .code("roomCode")
            .capacity(2)
            .build();

    @BeforeAll
    static void setup() {
        ReflectionTestUtils.setField(host, "id", 1L);
        ReflectionTestUtils.setField(member, "id", 2L);
        ReflectionTestUtils.setField(member2, "id", 3L);
        ReflectionTestUtils.setField(room, "id", 1L);
    }

    @Nested
    @DisplayName("createRoom 메소드는")
    class DescribeCreateRoom {

        @Nested
        @DisplayName("정상적인 RoomCreateRequest가 들어오면")
        class ContextWithValidRoomCreateRequest {

            @Test
            @DisplayName("방을 생성하고 참가한다.")
            void itCreatesRoom() {
                //given
                RoomCreateRequest request = new RoomCreateRequest("room", "code", 10);
                given(roomRepository.save(any(Room.class))).willReturn(room);
                given(joinsRepository.save(any(Joins.class))).willReturn(new Joins(host, room));

                //when
                roomService.createRoom(request, host);

                //then
                verify(roomRepository).save(any(Room.class));
                verify(joinsRepository).save(any(Joins.class));
            }
        }
    }

    @Nested
    @DisplayName("deleteRoom 메소드는")
    class DescribeDeleteRoom {

        @Nested
        @DisplayName("host 본인이 아니면")
        class ContextMemberIsNotHost {

            @Test
            @DisplayName("AccessDeniedException을 던진다.")
            void itThrowsAccessDeniedException () {
                //when, then
                assertThatThrownBy(() -> roomService.deleteRoom(room, member))
                        .isInstanceOf(AccessDeniedException.class);
            }
        }

        @Nested
        @DisplayName("host 본인이라면")
        class ContextMemberIsHost {

            @Test
            @DisplayName("방을 삭제한다.")
            void itDeletesRoom () {
                // given
                doNothing().when(roomRepository).delete(any(Room.class));

                // when
                roomService.deleteRoom(room, host);

                // then
                verify(roomRepository).delete(room);
            }
        }
    }

    @Nested
    @DisplayName("joinRoom 메소드는")
    class DescribeJoinRoom {

        @Nested
        @DisplayName("방의 정원이 다 찼으면")
        class ContextRoomWithFullCapacity {

            @Test
            @DisplayName("RoomCapacityExceededException을 던진다.")
            void itThrowsRoomCapacityExceededException() {
                room.getJoined().add(new Joins(host, room));
                room.getJoined().add(new Joins(member, room));

                given(memberRepository.findById(member2.getId())).willReturn(Optional.of(member2));

                assertThatThrownBy(() -> roomService.joinRoom(member2, room))
                        .isInstanceOf(RoomCapacityExceededException.class);
            }
        }

        @Nested
        @DisplayName("방의 정원이 차지 않았으면")
        class ContextRoomWithSeatsAvailable {

            @Test
            @DisplayName("방에 참가한다.")
            void itJoinsRoom() {
                room.getJoined().add(new Joins(host, room));
                Joins joins = new Joins(member, room);
                given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
                given(joinsRepository.save(any(Joins.class))).willReturn(joins);

                roomService.joinRoom(member, room);

                verify(joinsRepository).save(any(Joins.class));
            }
        }
    }

    @Nested
    @DisplayName("enterRoom 메소드는")
    class DescribeEnterRoom {

        @Nested
        @DisplayName("참가하지 않은 사람이 방에 입장하면")
        class ContextEnteringWhoDidNotJoinTheRoom {

            @Test
            @DisplayName("NotFoundException을 던진다.")
            void itThrowsNotFoundException() {
                assertThatThrownBy(() -> roomService.enterRoom(room, member))
                        .isInstanceOf(NotFoundException.class);
            }
        }

        @Nested
        @DisplayName("방의 정원이 차지 않았으면")
        class ContextEnteringWhoJoinedTheRoom {

            @Test
            @DisplayName("방에 입장하고 입장 시간을 업데이트한다.")
            void itEntersRoom() {
                Joins joins = mock(Joins.class);
                given(joinsRepository.findByRoomAndMember(any(Room.class), any(Member.class))).willReturn(Optional.of(joins));

                roomService.enterRoom(room, member);

                verify(joins).updateEnteredTime();
            }
        }
    }

    @Nested
    @DisplayName("getRoom 메소드는")
    class DescribeGetRoom {

        @Nested
        @DisplayName("존재하지 않는 방 ID를 검색하면")
        class ContextSearchingInvalidRoomId {

            @Test
            @DisplayName("NotFoundException을 던진다.")
            void itThrowsNotFoundException() {
                assertThatThrownBy(() -> roomService.getRoom(999L))
                        .isInstanceOf(NotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("getJoiningRooms 메소드는")
    class DescribeGetJoiningRooms {

        @Nested
        @DisplayName("정상적인 회원이라면")
        class ContextSearchingInvalidRoomId {

            @Test
            @DisplayName("List<Room>를 반환한다.")
            void itThrowsNotFoundException() {
                Member mockMember = mock(Member.class);
                Joins joins = new Joins(mockMember, room);
                mockMember.getJoins().add(joins);
                List<Joins> joinsList = List.of(joins);
                when(mockMember.getJoins()).thenReturn(joinsList);

                List<Room> joiningRooms = roomService.getJoiningRooms(mockMember);

                assertThat(joiningRooms).contains(room);
            }
        }
    }

    @Nested
    @DisplayName("findRoomByRoomCode 메소드는")
    class DescribeFindRoomByRoomCode {

        @Nested
        @DisplayName("존재하지 않는 방 코드를 검색하면")
        class ContextSearchingInvalidRoomId {

            @Test
            @DisplayName("NotFoundException을 던진다.")
            void itThrowsNotFoundException() {
                assertThatThrownBy(() -> roomService.findRoomByRoomCode("invalidCode"))
                        .isInstanceOf(NotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("doesRoomCodeExist 메소드는")
    class DescribeDoesRoomCodeExist {

        @Nested
        @DisplayName("존재하는 방 코드를 입력하면")
        class ContextSearchingInvalidRoomId {

            @Test
            @DisplayName("true를 반환한다.")
            void itThrowsNotFoundException() {
                String existingRoomCode = "existingRoom";
                when(roomRepository.findByCode(existingRoomCode)).thenReturn(Optional.of(new Room()));

                boolean result = roomService.doesRoomCodeExist(existingRoomCode);

                assertThat(result).isEqualTo(true);
                verify(roomRepository, times(1)).findByCode(existingRoomCode);
            }
        }
    }
}