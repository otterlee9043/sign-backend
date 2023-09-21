package com.sign.util;

import com.sign.domain.member.Role;
import com.sign.domain.member.entity.Member;
import com.sign.global.security.authentication.LoginMember;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.util.ReflectionTestUtils;

public class WithMockCustomLoginUserSecurityContextFactory
    implements WithSecurityContextFactory<WithMockCustomLoginUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomLoginUser annotation) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Member member = Member.builder()
                .username("username")
                .email("user@email.com")
                .role(Role.USER)
                .picture("profile-picture-url")
                .build();
        ReflectionTestUtils.setField(member, "id", 999L);

        UserDetails userDetails = new LoginMember(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        securityContext.setAuthentication(authentication);

        return securityContext;
    }
}
