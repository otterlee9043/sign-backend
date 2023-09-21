package com.sign.domain.member.service;

import com.sign.domain.member.Role;
import com.sign.domain.member.controller.dto.SignupRequest;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.repository.MemberRepository;
import com.sign.global.exception.DataDuplicateException;
import com.sign.global.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberServiceImpl memberService;

    private static final Member member = Member.builder()
            .username("username")
            .email("username@email.com")
            .password(null)
            .role(Role.USER)
            .provider("sign")
            .build();

    @Nested
    @DisplayName("join 메소드는")
    class DescribeJoin {

        @Nested
        @DisplayName("MemberCreateRequest의 email이 사용 중이라면")
        class ContextWithEmailDuplication {

            @Test
            @DisplayName("DataDuplicateException을 던진다.")
            void itThrowsDataDuplicateException() {
                SignupRequest request = new SignupRequest(
                        "existingUser",
                        "testPassword",
                        "testPassword",
                        "test@example.com");
                given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

                assertThrows(DataDuplicateException.class, () -> memberService.join(request));
            }

        }

        @Nested
        @DisplayName("MemberCreateRequest의 email을 아무도 사용하지 않는다면")
        class ContextWithValidEmail {

            @Test
            @DisplayName("회원을 생성한다.")
            void itCreatesMember() {
                SignupRequest request =
                        new SignupRequest("username", "", "", "username@email.com");

                given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());
                given(memberRepository.save(any(Member.class))).willReturn(member);

                memberService.join(request);

                verify(memberRepository).save(any(Member.class));
            }

        }
    }

    @Nested
    @DisplayName("findMember 메소드는")
    class DescribeFindMember {

        @Nested
        @DisplayName("Member의 id가 유효하지 않다면")
        class ContextWithInvalidId {

            @Test
            @DisplayName("NotFoundException을 던진다.")
            void itThrowsNotFoundException() {
                assertThatThrownBy(() -> memberService.findMember(999L))
                        .isInstanceOf(NotFoundException.class);
            }
        }
    }

}