package com.sign.global.security.authentication.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sign.domain.member.entity.Member;
import com.sign.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2MemberService {

    private final MemberRepository memberRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RestTemplate restTemplate = new RestTemplate();


    public Member loadMember(ClientRegistration clientRegistration, String token) {
        String userNameAttributeName = clientRegistration.getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> map = (Map<String, Object>) getOAuth2Attributes(clientRegistration, token);
        OAuth2Attributes attributes =
                OAuth2Attributes.of(clientRegistration.getRegistrationId(), userNameAttributeName, map);
        return saveOrUpdateMember(attributes);
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


    private Member saveOrUpdateMember(OAuth2Attributes attributes) {
        Member member = memberRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getUsername(), attributes.getPicture()))
                .orElse(attributes.toEntity());
        return memberRepository.save(member);
    }
}
