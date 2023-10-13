package com.sign.global.security.authentication;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Getter
public class OAuth2Token {

    private String accessToken;

    private String tokenType;

    private String expiresIn;

    private String scope;


    @Builder
    public OAuth2Token(String accessToken, String tokenType,
                       String expiresIn, String scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.scope = scope;
    }

    public static OAuth2Token of(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return ofGoogle(attributes);
        }

        return ofKakao(attributes);
    }

    public static OAuth2Token ofGoogle(Map<String, Object> attributes) {
        log.info("attributes: {}", attributes);
        return OAuth2Token.builder()
                .accessToken((String) attributes.get("access_token"))
                .tokenType((String) attributes.get("token_type"))
                .expiresIn((String) attributes.get("expires_in"))
                .scope((String) attributes.get("scope"))
                .build();
    }

    public static OAuth2Token ofKakao(Map<String, Object> attributes) {
        return OAuth2Token.builder()
                .accessToken((String) attributes.get("access_token"))
                .tokenType((String) attributes.get("token_type"))
                .expiresIn((String) attributes.get("expires_in"))
                .scope((String) attributes.get("scope"))
                .build();
    }
}
