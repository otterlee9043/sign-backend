package com.sign.domain.member.controller.dto;

import com.sign.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberProfile {
    private Long id;
    private String username;
    private String email;
    private String picture;

    public static MemberProfile from(Member member) {
        return MemberProfile.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .picture(member.getPicture())
                .build();
    }
}
