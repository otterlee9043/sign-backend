package com.sign.domain.classroom.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sign.domain.classroom.controller.dto.RoomCreateRequest;
import com.sign.domain.classroom.controller.dto.RoomUpdateRequest;
import com.sign.domain.classroom.entity.Room;
import com.sign.domain.classroom.service.RoomServiceImpl;
import com.sign.domain.member.Role;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.repository.MemberRepository;
import com.sign.domain.member.service.MemberServiceImpl;
import com.sign.global.exception.NotFoundException;
import com.sign.global.security.authentication.JwtProvider;
import com.sign.global.security.authentication.LoginMember;
import com.sign.global.security.config.TestSecurityConfig;
import com.sign.util.WithMockCustomLoginUser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;

import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles(profiles = {"test"})
@MockBean(JpaMetamodelMappingContext.class)
@WithMockCustomLoginUser
@Import({TestSecurityConfig.class})
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@WebMvcTest(controllers = RoomController.class)
class RoomControllerTest {

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private RoomServiceImpl roomService;

    @MockBean
    private MemberServiceImpl memberService;

    @MockBean
    private JwtProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    public static final String BASE_URL = "/api/v1";

    private static final Member host = Member.builder()
            .username("username123")
            .email("user123@email.com")
            .role(Role.USER)
            .build();

    private static final Member member = Member.builder()
            .username("username456")
            .email("user123@email.com")
            .role(Role.USER)
            .build();

    private static final Room room = Room.builder()
            .name("room123")
            .host(host)
            .code("1234")
            .capacity(2)
            .build();

    @BeforeAll
    static void init() {
        ReflectionTestUtils.setField(host, "id", 1L);
        ReflectionTestUtils.setField(member, "id", 2L);
        ReflectionTestUtils.setField(room, "id", 1L);
    }

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
               RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(provider))
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .apply(springSecurity())
                .build();
    }

    @Nested
    @WithMockCustomLoginUser
    @DisplayName("create 메소드는")
    class DescribeCreate {

        @Nested
        @DisplayName("유효하지 않은 RoomCreateRequest가 들어오면")
        class ContextWithInvalidRoomCreateRequest {

            @Test
            @DisplayName("Bad Request로 응답한다.")
            void itRespondsWithBadRequest() throws Exception {
                RoomCreateRequest createDTO = new RoomCreateRequest(
                        "room", "code", 99999
                );
                String content = objectMapper.writeValueAsString(createDTO);
                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .post(BASE_URL + "/classrooms")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);

                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isBadRequest())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("정상적인 RoomCreateRequest가 들어오면")
        class ContextWithValidRoomCreateRequest {

            @Test
            @DisplayName("Created로 응답한다.")
            void itRespondsWithCreated () throws Exception {
                RoomCreateRequest createDTO = new RoomCreateRequest(
                        "room", "code", 10
                );
                String content = objectMapper.writeValueAsString(createDTO);
                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .post(BASE_URL + "/classrooms")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON);

                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isCreated())
                        .andDo(document("방 생성",
                                preprocessRequest(prettyPrint()),
                                requestFields(
                                        fieldWithPath("name").description("사용자 이름"),
                                        fieldWithPath("code").description("비밀번호"),
                                        fieldWithPath("capacity").description("비밀번호 확인")
                                )))
                        .andDo(print());
            }
        }
    }

    @Nested
    @WithMockCustomLoginUser
    @DisplayName("findRoomByCode 메소드는")
    class DescribeFindRoomByCode {

        @Nested
        @DisplayName("존재하지 않는 방 코드를 받으면")
        class ContextWithInvalidRoomCode {

            @Test
            @DisplayName("Not Found로 응답한다.")
            void itRespondsWithNotFound() throws Exception {
                given(roomService.findRoomByRoomCode(anyString())).willThrow(NotFoundException.class);

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .get(BASE_URL + "/classrooms")
                        .param("code", "1234")
                        .accept(MediaType.APPLICATION_JSON);
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isNotFound())
                        .andDo(print());
                verify(roomService).findRoomByRoomCode(anyString());
            }
        }

        @Nested
        @DisplayName("존재하는 방 코드를 받으면")
        class ContextWithValidRoomCode {

            @Test
            @DisplayName("RoomResponse와 함께 OK로 응답한다.")
            void itReturnsRoomResponse() throws Exception {
                given(roomService.findRoomByRoomCode(anyString())).willReturn(room);

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .get(BASE_URL + "/classrooms")
                        .param("code", "1234")
                        .accept(MediaType.APPLICATION_JSON);
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isOk())
                        .andDo(document("방 검색",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParameters(
                                        parameterWithName("code").description("방 코드")
                                ),
                                responseFields(
                                        fieldWithPath("id")
                                                .type(JsonFieldType.NUMBER)
                                                .description("방 식별자"),
                                        fieldWithPath("roomName")
                                                .type(JsonFieldType.STRING)
                                                .description("방 이름"),
                                        fieldWithPath("capacity")
                                                .type(JsonFieldType.NUMBER)
                                                .description("정원"),
                                        fieldWithPath("hostUsername")
                                                .type(JsonFieldType.STRING)
                                                .description("호스트 이름"),
                                        fieldWithPath("hostEmail")
                                                .type(JsonFieldType.STRING)
                                                .description("호스트 이메일")
                                )))
                        .andDo(print());
                verify(roomService).findRoomByRoomCode(anyString());
            }
        }
    }

    @Nested
    @WithMockCustomLoginUser
    @DisplayName("getRoom 메소드는")
    class DescribeGetRoom {

        @Nested
        @DisplayName("존재하는 방 id를 받으면")
        class ContextWithValidRoomId {

            @Test
            @DisplayName("RoomResponse와 함께 OK로 응답한다.")
            void itReturnsRoomResponse() throws Exception {
                given(roomService.getRoom(anyLong())).willReturn(room);

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .get(BASE_URL + "/classroom/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON);
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isOk())
                        .andDo(document("방 정보 조회",
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("id").description("방 ID")
                                ),
                                responseFields(
                                        fieldWithPath("id")
                                                .type(JsonFieldType.NUMBER)
                                                .description("방 식별자"),
                                        fieldWithPath("roomName")
                                                .type(JsonFieldType.STRING)
                                                .description("방 이름"),
                                        fieldWithPath("capacity")
                                                .type(JsonFieldType.NUMBER)
                                                .description("정원"),
                                        fieldWithPath("hostUsername")
                                                .type(JsonFieldType.STRING)
                                                .description("호스트 이름"),
                                        fieldWithPath("hostEmail")
                                                .type(JsonFieldType.STRING)
                                                .description("호스트 이메일")
                                )))
                        .andDo(print());
            }
        }
    }

    @Nested
    @WithMockCustomLoginUser
    @DisplayName("updateRoom 메소드는")
    class DescribeUpdateRoom {

        @Nested
        @DisplayName("host가 아닌 회원이 방을 수정하면")
        class ContextThatMemberIsNotHost {

            @Test
            @DisplayName("Forbidden으로 응답한다.")
            void itThrowsAccessDeniedException() throws Exception {
                RoomUpdateRequest updateDTO = new RoomUpdateRequest("newName");
                String content = objectMapper.writeValueAsString(updateDTO);
                given(roomService.getRoom(anyLong())).willReturn(room);
                doThrow(new AccessDeniedException("권한 없음"))
                        .when(memberService).verifyMemberAccess(anyLong(), any(LoginMember.class));

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .put(BASE_URL + "/classroom/{id}", 1)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isForbidden())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("host가 방을 수정하면")
        class ContextThatMemberIsHost {

            @Test
            @DisplayName("방을 수정하고 Ok로 응답한다.")
            void itRespondsWithOk() throws Exception {
                RoomUpdateRequest updateDTO = new RoomUpdateRequest("newName");
                String content = objectMapper.writeValueAsString(updateDTO);
                given(roomService.getRoom(anyLong())).willReturn(room);

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .put(BASE_URL + "/classroom/{id}", 1)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON);
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isOk())
                        .andDo(document("방 정보 수정",
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("id").description("방 ID")
                                ),
                                requestFields(
                                        fieldWithPath("roomName")
                                                .type(JsonFieldType.STRING)
                                                .description("방 이름")
                                )))
                        .andDo(print());
            }
        }
    }

    @Nested
    @WithMockCustomLoginUser
    @DisplayName("deleteRoom 메소드는")
    class DescribeDeleteRoom {

        @Nested
        @DisplayName("host가 방을 삭제하면")
        class ContextThatMemberIsHost {

            @Test
            @DisplayName("방을 삭제하고 OK로 응답한다.")
            void itDeletesRoom() throws Exception {
                given(roomService.getRoom(anyLong())).willReturn(room);

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .delete(BASE_URL + "/classroom/{id}", 1);
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isOk())
                        .andDo(document("방 삭제",
                                pathParameters(
                                        parameterWithName("id").description("방 ID")
                                )))
                        .andDo(print());
            }

        }
    }

    @Nested
    @DisplayName("checkRoomCode 메소드는")
    class DescribeCheckRoomCode {

        @Nested
        @DisplayName("존재하는 방 코드를 받으면")
        class ContextThatRoomCodeAlreadyExists {

            @Test
            @DisplayName("Conflict로 응답한다.")
            void itRespondsWithConflict() throws Exception {
                given(roomService.doesRoomCodeExist(anyString())).willReturn(true);

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .get(BASE_URL + "/classrooms/code/{code}/duplication", "1234");
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isConflict())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("방 코드가 존재하지 않다면")
        class ContextThatRoomCodeDoesNotExists {

            @Test
            @DisplayName("Ok로 응답한다.")
            void itRespondsWithOk() throws Exception {
                given(roomService.doesRoomCodeExist(anyString())).willReturn(false);

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .get(BASE_URL + "/classrooms/code/{code}/duplication", "1234");
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isOk())
                        .andDo(document("방 코드 중복 확인",
                                pathParameters(
                                        parameterWithName("code").description("방 코드")
                                )))
                        .andDo(print());
            }
        }
    }
}

