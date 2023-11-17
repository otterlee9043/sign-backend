package com.sign.global.security.authentication.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sign.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2LoginService  {

    private final InMemoryClientRegistrationRepository clientRegistrationRepository;

    private final OAuth2MemberService oAuth2MemberService;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();


    public String getAuthorizationURI(String registrationId) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        return clientRegistration.getProviderDetails().getAuthorizationUri()
                + "?client_id=" + clientRegistration.getClientId()
                + "&response_type=code"
                + "&redirect_uri=" + clientRegistration.getRedirectUri()
                + "&scope=" + String.join("%20", clientRegistration.getScopes());
    }


    public Member login(String registrationId, String code){
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        OAuthToken oAuthToken = getAccessToken(registrationId, clientRegistration, code);
        return oAuth2MemberService.loadMember(clientRegistration, oAuthToken.getAccessToken());
    }


    private OAuthToken getAccessToken(String registrationId, ClientRegistration clientRegistration, String code){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> tokenRequest =
                new HttpEntity<>(setParamsForTokenRequest(clientRegistration, code), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(clientRegistration.getProviderDetails().getTokenUri(),
                tokenRequest, String.class);

        Map tokenAttributes = parseTokenResponse(response.getBody());
        return OAuthToken.of(registrationId, tokenAttributes);
    }


    private MultiValueMap<String, String> setParamsForTokenRequest
            (ClientRegistration clientRegistration, String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("grant_type", clientRegistration.getAuthorizationGrantType().getValue());
        params.add("redirect_uri", clientRegistration.getRedirectUri());
        params.add("client_id", clientRegistration.getClientId());
        params.add("client_secret", clientRegistration.getClientSecret());

        return params;
    }


    private Map parseTokenResponse(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
