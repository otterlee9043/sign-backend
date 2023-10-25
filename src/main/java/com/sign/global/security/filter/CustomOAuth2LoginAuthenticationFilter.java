package com.sign.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sign.domain.member.entity.Member;
import com.sign.global.security.authentication.oauth2.OAuth2LoginService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class CustomOAuth2LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_LOGIN_REQUEST_URL = "/api/v1/oauth2/authorization/*";

    private static final String HTTP_METHOD = "GET";

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final OAuth2LoginService oAuth2LoginService;

    private final InMemoryClientRegistrationRepository clientRegistrationRepository;


    public CustomOAuth2LoginAuthenticationFilter
            (InMemoryClientRegistrationRepository clientRegistrationRepository,
             OAuth2LoginService oAuth2LoginService) {
        super(new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD));
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.oAuth2LoginService = oAuth2LoginService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String code = request.getParameter("code");
        String pathInfo = request.getPathInfo();
        String provider = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);

        Member member = oAuth2LoginService.login(provider, code);
        oAuth2LoginService.sendAccessTokenAndRefreshToken(member, response);

        return null;
    }


}
