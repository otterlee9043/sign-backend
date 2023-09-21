package com.sign.domain.member.repository;

import com.sign.domain.member.Role;
import com.sign.domain.member.entity.Member;
import org.junit.jupiter.api.*;
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

    private static final Member member = Member.builder()
            .username("user1234")
            .email("user1234@email.com")
            .role(Role.USER)
            .build();

    @BeforeEach
    void cleanup() {
        memberRepository.deleteAll();
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
                Member savedMember = memberRepository.save(member);

                //when
                Optional<Member> foundMember = memberRepository.findByUsername(savedMember.getUsername());

                //then
                assertThat(savedMember).isEqualTo(foundMember.get());
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
                Member savedMember = memberRepository.save(member);

                //when
                Optional<Member> foundMember = memberRepository.findByEmail(savedMember.getEmail());

                //then
                assertThat(savedMember).isEqualTo(foundMember.get());
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
                Member savedMember = memberRepository.save(member);
                String refreshToken = "eyJhbGciOiJpXVCJ9.eyJpYXQMjJ9.tbDepxfz48wgRQ";
                member.updateRefreshToken(refreshToken);

                //when
                Optional<Member> foundMember = memberRepository.findByRefreshToken(refreshToken);

                //then
                assertThat(savedMember).isEqualTo(foundMember.get());
            }
        }
    }

}