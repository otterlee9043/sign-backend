package com.sign.domain.member.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sign.domain.classroom.entity.Room;
import com.sign.domain.classroom.service.RoomServiceImpl;
import com.sign.domain.member.Role;
import com.sign.domain.member.controller.dto.SignupRequest;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.repository.MemberRepository;
import com.sign.domain.member.service.MemberServiceImpl;
import com.sign.global.exception.NotFoundException;
import com.sign.global.security.authentication.LoginMember;
import com.sign.global.security.config.TestSecurityConfig;
import com.sign.util.WithMockCustomLoginUser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(profiles = {"test"})
@MockBean(JpaMetamodelMappingContext.class)
@Import({TestSecurityConfig.class})
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {

    @MockBean
    private RoomServiceImpl roomService;

    @MockBean
    private MemberServiceImpl memberService;

    @MockBean
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private static final Member member = Member.builder()
            .username("username1")
            .email("user1@email.com")
            .role(Role.USER)
            .picture("profile-picture-url")
            .build();

    private static final Room room = Room.builder()
            .name("room1")
            .host(member)
            .code("1234")
            .capacity(2)
            .build();
    public static final String BASE_URL = "/api/v1";

    @BeforeAll
    static void init() {
        ReflectionTestUtils.setField(member, "id", 1L);
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
    @DisplayName("signup 메소드는")
    @WithAnonymousUser
    class DescribeSignup {
        @Nested
        @DisplayName("비정상적인 SignupRequest가 들어오면")
        class ContextWithInvalidSignupRequest {

            @Test
            @DisplayName("Bad Request로 응답한다.")
            void itRespondsWithBadRequest() throws Exception {
                SignupRequest createDTO = new SignupRequest(
                        "username",
                        "Password!",
                        "Password!",
                        ""
                );
                String content = objectMapper.writeValueAsString(createDTO);
                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .post(BASE_URL + "/members")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);

                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isBadRequest())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("정상적인 SignupRequest가 들어오면")
        class ContextWithValidSignupRequest {

            @Test
            @DisplayName("회원을 생성한다.")
            void itCreatesMember() throws Exception {
                SignupRequest createDTO = new SignupRequest(
                        "sign2023",
                        "Password12!",
                        "Password12!",
                        "sign2023@email.com"
                );
                String content = objectMapper.writeValueAsString(createDTO);
                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .post(BASE_URL + "/members")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON);
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isCreated())
                        .andDo(document("회원가입",
                                preprocessRequest(prettyPrint()),
                                requestFields(
                                        fieldWithPath("username")
                                                .type(JsonFieldType.STRING)
                                                .description("사용자 이름"),
                                        fieldWithPath("password")
                                                .type(JsonFieldType.STRING)
                                                .description("비밀번호"),
                                        fieldWithPath("password2")
                                                .type(JsonFieldType.STRING)
                                                .description("비밀번호 확인"),
                                        fieldWithPath("email")
                                                .type(JsonFieldType.STRING)
                                                .description("이메일")
                                )))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("getMyProfile 메소드는")
    @WithMockCustomLoginUser
    class DescribeGetMyProfile {

        @Test
        @DisplayName("로그인되어 있다면 프로필을 반환한다.")
        void itReturnsMemberProfile() throws Exception {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Member loginMember = ((LoginMember) authentication.getPrincipal()).getMember();

            MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                    .get(BASE_URL + "/member")
                    .header("Access-Token", "your-access-token")
                    .accept(MediaType.APPLICATION_JSON);
            ResultActions response = mockMvc.perform(request);

            response.andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value(loginMember.getUsername()))
                    .andExpect(jsonPath("$.email").value(loginMember.getEmail()))
                    .andDo(document("로그인한 회원 정보 조회",
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("id")
                                            .type(JsonFieldType.NUMBER)
                                            .description("사용자 식별자"),
                                    fieldWithPath("username")
                                            .type(JsonFieldType.STRING)
                                            .description("사용자 이름"),
                                    fieldWithPath("email")
                                            .type(JsonFieldType.STRING)
                                            .description("이메일"),
                                    fieldWithPath("picture")
                                            .type(JsonFieldType.STRING)
                                            .description("프로필 이미지")
                            )))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("unregister 메소드는")
    @WithMockCustomLoginUser
    class DescribeUnregister {

        @Nested
        @DisplayName("탈퇴하려는 회원 본인이라면")
        class ContextWithOwner {

            @Test
            @DisplayName("탈퇴하고 Ok로 응답한다.")
            void itUnregisters() throws Exception {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Member loginMember = ((LoginMember) authentication.getPrincipal()).getMember();

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .delete(BASE_URL + "/member")
                        .header("Access-Token", "your-access-token");
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isOk())
                        .andDo(document("회원 탈퇴"))
                        .andDo(print());
                verify(memberService).deleteMember(loginMember);
            }
        }
    }

    @Nested
    @DisplayName("getProfile 메소드는")
    @WithMockCustomLoginUser
    class DescribeGetProfile {

        @Nested
        @DisplayName("존재하지 않는 회원 ID가 들어오면")
        class ContextWithInvalidMemberId {

            @Test
            @DisplayName("Not Found로 응답한다.")
            void itRespondsWithNotFound() throws Exception {
                given(memberService.findMember(anyLong())).willThrow(NotFoundException.class);

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .get(BASE_URL + "/member/{id}", 2)
                        .header("Access-Token", "your-access-token")
                        .accept(MediaType.APPLICATION_JSON);
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isNotFound())
                        .andDo(print());
                verify(memberService).findMember(anyLong());
            }
        }

        @Nested
        @DisplayName("존재하는 회원 ID가 들어오면")
        class ContextWithValidMemberId {

            @Test
            @DisplayName("프로필과 함께 Ok로 응답한다.")
            void itReturnsProfile() throws Exception {
                given(memberService.findMember(anyLong())).willReturn(member);

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .get(BASE_URL + "/member/{id}", 1)
                        .header("Access-Token", "your-access-token")
                        .accept(MediaType.APPLICATION_JSON);
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isOk())
                        .andExpect(jsonPath("$.username").value(member.getUsername()))
                        .andExpect(jsonPath("$.email").value(member.getEmail()))
                        .andDo(document("회원 정보 조회",
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("id").description("회원 ID")
                                ),
                                responseFields(
                                        fieldWithPath("id")
                                                .type(JsonFieldType.NUMBER)
                                                .description("사용자 식별자"),
                                        fieldWithPath("username")
                                                .type(JsonFieldType.STRING)
                                                .description("사용자 이름"),
                                        fieldWithPath("email")
                                                .type(JsonFieldType.STRING)
                                                .description("이메일"),
                                        fieldWithPath("picture")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 이미지")
                                )))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("getJoiningRooms 메소드는")
    @WithMockCustomLoginUser
    class DescribeGetJoiningRooms {

        @Nested
        @DisplayName("회원 본인이 아니라면")
        class ContextWhoIsNotOwner {

            @Test
            @DisplayName("Forbidden으로 응답한다.")
            void itRespondsWithNotFound() throws Exception {
                doThrow(new AccessDeniedException("권한 없음"))
                        .when(memberService).verifyMemberAccess(anyLong(), any(LoginMember.class));

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .get(BASE_URL + "/member/{memberId}/classrooms", 1)
                        .header("Access-Token", "your-access-token")
                        .accept(MediaType.APPLICATION_JSON);
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isForbidden())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("회원 본인이라면")
        class ContextWithOwner {

            @Test
            @DisplayName("RoomResponse 리스트와 함께 Ok로 응답한다.")
            void itReturnsRoomResponseList() throws Exception {
                given(roomService.getJoiningRooms(any(Member.class))).willReturn(List.of(room));

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .get(BASE_URL + "/member/{memberId}/classrooms", 1)
                        .header("Access-Token", "your-access-token")
                        .accept(MediaType.APPLICATION_JSON);
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isOk())
                        .andExpect(jsonPath("$").isArray())
                        .andExpect(jsonPath("$[0].id").value(room.getId()))
                        .andExpect(jsonPath("$[0].roomName").value(room.getName()))
                        .andDo(document("참여한 방 조회",
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("memberId").description("회원 ID")
                                ),
                                responseFields(
                                        fieldWithPath("[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("참가한 방 리스트"),
                                        fieldWithPath("[].id")
                                                .type(JsonFieldType.NUMBER)
                                                .description("방 식별자"),
                                        fieldWithPath("[].roomName")
                                                .type(JsonFieldType.STRING)
                                                .description("방 이름"),
                                        fieldWithPath("[].capacity")
                                                .type(JsonFieldType.NUMBER)
                                                .description("정원"),
                                        fieldWithPath("[].hostUsername")
                                                .type(JsonFieldType.STRING)
                                                .description("호스트 이름"),
                                        fieldWithPath("[].hostEmail")
                                                .type(JsonFieldType.STRING)
                                                .description("호스트 이메일")
                                )))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("join 메소드는")
    @WithMockCustomLoginUser
    class DescribeJoin {

        @Nested
        @DisplayName("회원 본인이라면")
        class ContextWithOwner {

            @Test
            @DisplayName("Ok로 응답한다.")
            void itRespondsWithOk() throws Exception {
                given(roomService.getRoom(anyLong())).willReturn(room);
                given(memberService.findMember(anyLong())).willReturn(member);

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .put(BASE_URL + "/member/{memberId}/classroom/{roomId}", 1, 1)
                        .header("Access-Token", "your-access-token")
                        .accept(MediaType.APPLICATION_JSON);
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isOk())
                        .andDo(document("방 참여",
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("memberId").description("회원 ID"),
                                        parameterWithName("roomId").description("방 ID")
                                )))
                        .andDo(print());
                verify(roomService).joinRoom(member, room);
            }
        }
    }

    @Nested
    @DisplayName("enter 메소드는")
    @WithMockCustomLoginUser
    class DescribeEnter {

        @Nested
        @DisplayName("회원 본인이라면")
        class ContextWithOwner {

            @Test
            @DisplayName("Ok로 응답한다.")
            void itRespondsWithOk() throws Exception {
                given(roomService.getRoom(anyLong())).willReturn(room);
                given(memberService.findMember(anyLong())).willReturn(member);

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .patch(BASE_URL + "/member/{memberId}/classroom/{roomId}", 1, 1)
                        .header("Access-Token", "your-access-token")
                        .accept(MediaType.APPLICATION_JSON);
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isOk())
                        .andDo(document("방 입장",
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("memberId").description("회원 ID"),
                                        parameterWithName("roomId").description("방 ID")
                                )))
                        .andDo(print());
                verify(roomService).enterRoom(room, member);
            }
        }
    }

    @Nested
    @DisplayName("checkEmail 메소드는")
    @WithAnonymousUser
    class DescribeCheckEmail {

        @Nested
        @DisplayName("존재하는 이메일을 받으면")
        class ContextThatEmailAlreadyExists {
            @Test
            @DisplayName("Conflict로 응답한다.")
            void itRespondsWithConflict() throws Exception {
                given(memberService.doesEmailExist(anyString())).willReturn(true);

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .get(BASE_URL + "/members/email/{email}/duplication", "username@email.com")
                        .header("Access-Token", "your-access-token");
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isConflict())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("이메일이 존재하지 않다면")
        class ContextThatEmailDoesNotExist {
            @Test
            @DisplayName("Ok로 응답한다.")
            void itRespondsWithOk() throws Exception {
                given(memberService.doesEmailExist(anyString())).willReturn(false);

                MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
                        .get(BASE_URL + "/members/email/{email}/duplication", "username@email.com")
                        .header("Access-Token", "your-access-token");
                ResultActions response = mockMvc.perform(request);

                response.andExpect(status().isOk())
                        .andDo(document("이메일 중복 확인",
                                pathParameters(
                                        parameterWithName("email").description("회원 가입시 중복 여부를 확인할 이메일")
                                )))
                        .andDo(print());
            }
        }
    }
}

