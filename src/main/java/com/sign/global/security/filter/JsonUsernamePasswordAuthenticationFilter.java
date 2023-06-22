package com.sign.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class JsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper objectMapper;
    public JsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper){
        super("/api/member/login");
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        if (request.getContentType() == null || !request.getContentType().equals("application/json")) {
            log.error("filter content type 오류");
            throw new AuthenticationServiceException("Content type 오류");
        }
        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);

        String email = usernamePasswordMap.get("email");
        String password = usernamePasswordMap.get("password");
        UsernamePasswordAuthenticationToken authRequest
                = new UsernamePasswordAuthenticationToken(email, password);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
