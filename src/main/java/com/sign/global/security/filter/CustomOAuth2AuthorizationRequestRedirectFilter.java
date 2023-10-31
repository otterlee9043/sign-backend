package com.sign.global.security.filter;

import com.sign.global.security.authentication.oauth2.OAuth2LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2AuthorizationRequestRedirectFilter extends OncePerRequestFilter {

    public static final String DEFAULT_AUTHORIZATION_REQUEST_BASE_URI = "/oauth2/authorization";

    private static final String REGISTRATION_ID_URI_VARIABLE_NAME = "provider";

    private static final String HTTP_METHOD = "GET";

    private static final RequestMatcher requestMatcher =
            new AntPathRequestMatcher(DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/{" + REGISTRATION_ID_URI_VARIABLE_NAME + "}",
                    HTTP_METHOD);

    private final OAuth2LoginService oAuth2LoginService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (requestMatcher.matches(request)) {
            String registrationId = retrieveRegistrationId(request);
            String authorizationURI = oAuth2LoginService.getAuthorizationURI(registrationId);
            response.sendRedirect(authorizationURI);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String retrieveRegistrationId(HttpServletRequest request) {
        return requestMatcher.matcher(request).getVariables()
                .get(REGISTRATION_ID_URI_VARIABLE_NAME);
    }
}
