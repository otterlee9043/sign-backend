package com.sign.global.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2LoginService {
    private final InMemoryClientRegistrationRepository clientRegistrationRepository;

    private final JwtProvider jwtProvider;

    private final MemberRepository memberRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();


    public String getAuthorizationURI(String provider) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(provider);
        return clientRegistration.getProviderDetails().getAuthorizationUri()
                + "?client_id=" + clientRegistration.getClientId()
                + "&response_type=code"
                + "&redirect_uri=" + clientRegistration.getRedirectUri()
                + "&scope=" + String.join("%20", clientRegistration.getScopes());
    }

    public Member login(String provider, String code){
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(provider);
        KakaoToken kakaoToken = getAccessToken(clientRegistration, code);
        Member member = loadUser(clientRegistration, kakaoToken.getAccess_token());
        return member;
    }

    public KakaoToken getAccessToken(ClientRegistration clientRegistration, String code){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> tokenRequest =
                new HttpEntity<>(getParamsForTokenRequest(clientRegistration, code), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(clientRegistration.getProviderDetails().getTokenUri(),
                tokenRequest, String.class);
        try {
            return objectMapper.readValue(response.getBody(), KakaoToken.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Member loadUser(ClientRegistration clientRegistration, String token) {
        String userNameAttributeName = clientRegistration.getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> map = (Map<String, Object>) getOAuth2Attributes(clientRegistration, token);
        OAuthAttributes attributes = OAuthAttributes.of(clientRegistration.getRegistrationId(), userNameAttributeName, map);
        Member member = saveOrUpdate(attributes);
        return member;
    }

    private Map<?, ?> getOAuth2Attributes(ClientRegistration clientRegistration, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri(),
                HttpMethod.GET, request, String.class);
        try {
            return objectMapper.readValue(response.getBody(), Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member member = memberRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getUsername(), attributes.getPicture()))
                .orElse(attributes.toEntity());
        return memberRepository.save(member);
    }
    private MultiValueMap<String, String> getParamsForTokenRequest(ClientRegistration clientRegistration, String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("grant_type", clientRegistration.getAuthorizationGrantType().getValue());
        params.add("redirect_uri", clientRegistration.getRedirectUri());
        params.add("client_id", clientRegistration.getClientId());
        params.add("client_secret", clientRegistration.getClientSecret());

        return params;
    }

    public void sendAccessTokenAndRefreshToken(Member member, HttpServletResponse response) {
        String accessToken = jwtProvider.createAccessToken(member.getEmail());
        String refreshToken = jwtProvider.createRefreshToken();

        jwtProvider.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtProvider.updateRefreshToken(member.getEmail(), refreshToken);
    }
}
