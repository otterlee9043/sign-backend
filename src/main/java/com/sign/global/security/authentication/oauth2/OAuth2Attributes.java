package com.sign.global.security.authentication.oauth2;

import com.sign.domain.member.Role;
import com.sign.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Getter
@ToString
public class OAuth2Attributes {

    private Map<String, Object> attributes;

    private String nameAttributeKey;

    private String username;

    private String email;

    private String picture;

    private String provider;


    @Builder
    public OAuth2Attributes(Map<String, Object> attributes, String nameAttributeKey,
                            String username, String email, String picture, String provider){
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.username = username;
        this.email = email;
        this.picture = picture;
        this.provider = provider;
    }

    public static OAuth2Attributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes){
        if ("kakao".equals(registrationId)){
            return ofKakao("id", attributes);
        }

        return ofGoogle(userNameAttributeName, attributes);
    }

    public static OAuth2Attributes ofKakao(String userNameAttributeNAme, Map<String, Object> attributes){
        Map<String, Object> accountInfo = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) accountInfo.get("profile");
        return OAuth2Attributes.builder()
                .username((String) profile.get("nickname"))
                .email((String) accountInfo.get("email"))
                .picture((String) profile.get("profile_image_url"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeNAme)
                .provider("kakao")
                .build();
    }

    private static OAuth2Attributes ofGoogle(String userNameAttributeNAme, Map<String, Object> attributes){
        return OAuth2Attributes.builder()
                .username((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeNAme)
                .provider("google")
                .build();
    }

    public Member toEntity(){
        return Member.builder()
                .username(username)
                .email(email)
                .picture(picture)
                .role(Role.USER)
                .provider(provider)
                .build();
    }
}
