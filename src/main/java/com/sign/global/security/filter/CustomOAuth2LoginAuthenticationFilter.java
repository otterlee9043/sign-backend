package com.sign.global.security.filter;

import com.sign.domain.member.entity.Member;
import com.sign.global.security.authentication.LoginMember;
import com.sign.global.security.authentication.oauth2.OAuth2LoginService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class CustomOAuth2LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_LOGIN_REQUEST_URI = "/login/oauth2/code";

    private static final String REGISTRATION_ID_URI_VARIABLE_NAME = "provider";

    private static final String HTTP_METHOD = "GET";

    private static final RequestMatcher requestMatcher =
            new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URI + "/{" + REGISTRATION_ID_URI_VARIABLE_NAME + "}",
                    HTTP_METHOD);

    private final OAuth2LoginService oAuth2LoginService;


    public CustomOAuth2LoginAuthenticationFilter(OAuth2LoginService oAuth2LoginService) {
        super(requestMatcher);
        this.oAuth2LoginService = oAuth2LoginService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String code = request.getParameter("code");
        String registrationId = retrieveRegistrationId(request);
        Member member = oAuth2LoginService.login(registrationId, code);

        UserDetails userDetails = new LoginMember(member);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String retrieveRegistrationId(HttpServletRequest request) {
        return requestMatcher.matcher(request).getVariables()
                .get(REGISTRATION_ID_URI_VARIABLE_NAME);
    }
}
