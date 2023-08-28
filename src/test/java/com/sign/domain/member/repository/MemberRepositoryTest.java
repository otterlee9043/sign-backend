package com.sign.domain.member.repository;

import com.sign.domain.member.Role;
import com.sign.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    private Member getMember(String username, String email) {
        Member member = Member.builder()
                .username(username)
                .email(email)
                .role(Role.USER)
                .build();
        return memberRepository.save(member);
    }

    @Nested
    @DisplayName("findByUsername 메소드는")
    class DescribeFindByUsername {

        @Nested
        @DisplayName("존재하지 않는 username이라면")
        class ContextWithNonExistentUsername {

            @Test
            @DisplayName("Optional.empty()를 반환한다.")
            void itReturnsOptionalEmpty() {
                //given
                //when
                Optional<Member> foundMember = memberRepository.findByUsername("username");

                //then
                assertThat(foundMember).isEmpty();
            }
        }

        @Nested
        @DisplayName("올바른 username이라면")
        class ContextWithValidUsername {

            @Test
            @DisplayName("Member가 포함된 Optional을 반환한다.")
            void itReturnsOptionalMember() {
                //given
                Member member = getMember("username", "user@email.com");

                //when
                Optional<Member> foundMember = memberRepository.findByUsername("username");

                //then
                assertThat(member).isEqualTo(foundMember.get());
            }
        }
    }

    @Nested
    @DisplayName("findByEmail 메소드는")
    class DescribeFindByEmail {

        @Nested
        @DisplayName("존재하지 않는 email이라면")
        class ContextWithNonExistentEmail {

            @Test
            @DisplayName("Optional.empty()를 반환한다.")
            void itReturnsOptionalEmpty() {
                //given
                //when
                Optional<Member> foundMember = memberRepository.findByEmail("user@email.com");

                //then
                assertThat(foundMember).isEmpty();
            }
        }

        @Nested
        @DisplayName("올바른 email이라면")
        class ContextWithValidUsername {

            @Test
            @DisplayName("Member가 포함된 Optional을 반환한다.")
            void itReturnsOptionalMember() {
                //given
                Member member = getMember("username", "user@email.com");

                //when
                Optional<Member> foundMember = memberRepository.findByEmail("user@email.com");

                //then
                assertThat(member).isEqualTo(foundMember.get());
            }
        }
    }

    @Nested
    @DisplayName("findByRefreshToken 메소드는")
    class DescribeFindByRefreshToken {

        @Nested
        @DisplayName("유효하지 않은 RefreshToken이라면")
        class ContextWithInvalidRefreshToken {

            @Test
            @DisplayName("Optional.empty()를 반환한다.")
            void itReturnsOptionalEmpty() {
                //given
                //when
                Optional<Member> foundMember = memberRepository.findByRefreshToken("refreshToken");

                //then
                assertThat(foundMember).isEmpty();
            }
        }

        @Nested
        @DisplayName("유효한 refreshToken이라면")
        class ContextWithValidRefreshToken {

            @Test
            @DisplayName("Member가 포함된 Optional을 반환한다.")
            void itReturnsOptionalMember() {
                //given
                Member member = getMember("username", "user@email.com");
                String refreshToken = "eyJhbGciOiJpXVCJ9.eyJpYXQMjJ9.tbDepxfz48wgRQ";
                member.updateRefreshToken(refreshToken);

                //when
                Optional<Member> foundMember = memberRepository.findByRefreshToken(refreshToken);

                //then
                assertThat(member).isEqualTo(foundMember.get());
            }
        }
    }

}