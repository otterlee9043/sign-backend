package com.sign.security;

import com.sign.domain.member.Role;
import com.sign.domain.member.entity.Authority;
import com.sign.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;

@Getter
@ToString
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String username;
    private String email;
    private String picture;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String username, String email, String picture){
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.username = username;
        this.email = email;
        this.picture = picture;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes){
        if ("kakao".equals(registrationId)){
            return ofKakao("id", attributes);
        }

        return ofGoogle(userNameAttributeName, attributes);
    }

    public static OAuthAttributes ofKakao(String userNameAttributeNAme, Map<String, Object> attributes){
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        System.out.println("KAKAO - emeil:" + (String) response.get("email"));
        return OAuthAttributes.builder()
                .username((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeNAme)
                .build();
    }

    // map 객체를 OAuthAttributes 객체로 전환
    private static OAuthAttributes ofGoogle(String userNameAttributeNAme, Map<String, Object> attributes){
        return OAuthAttributes.builder()
                .username((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeNAme)
                .build();
    }

    public Member toEntity(){
        return Member.builder()
                .username(username)
                .email(email)
                .picture(picture)
                .role(Role.USER)
                .build();
    }
}
