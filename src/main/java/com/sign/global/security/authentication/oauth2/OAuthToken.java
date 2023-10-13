package com.sign.global.security.authentication.oauth2;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Getter
public class OAuthToken {

    private String accessToken;

    private String tokenType;

    private Integer expiresIn;

    private String scope;


    @Builder
    public OAuthToken(String accessToken, String tokenType,
                      Integer expiresIn, String scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.scope = scope;
    }

    public static OAuthToken of(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return ofGoogle(attributes);
        }

        return ofKakao(attributes);
    }

    public static OAuthToken ofGoogle(Map<String, Object> attributes) {
        return OAuthToken.builder()
                .accessToken((String) attributes.get("access_token"))
                .tokenType((String) attributes.get("token_type"))
                .expiresIn((Integer) attributes.get("expires_in"))
                .scope((String) attributes.get("scope"))
                .build();
    }

    public static OAuthToken ofKakao(Map<String, Object> attributes) {
        return OAuthToken.builder()
                .accessToken((String) attributes.get("access_token"))
                .tokenType((String) attributes.get("token_type"))
                .expiresIn((Integer) attributes.get("expires_in"))
                .scope((String) attributes.get("scope"))
                .build();
    }

}
