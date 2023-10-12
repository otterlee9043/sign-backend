package com.sign.global.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sign.domain.member.repository.MemberRepository;
import com.sign.global.exception.ErrorResult;
import com.sign.global.security.authentication.JwtProvider;
import com.sign.global.security.authentication.MemberSecurityService;
import com.sign.global.security.authentication.OAuth2MemberService;
import com.sign.global.security.filter.JsonUsernamePasswordAuthenticationFilter;
import com.sign.global.security.filter.JwtAuthenticationFilter;
import com.sign.global.security.handler.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final MemberRepository memberRepository;

    private final JwtProvider jwtProvider;

    private final OAuth2MemberService oAuth2UserService;

    private final MemberSecurityService memberSecurityService;

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .headers().disable()
                .cors(withDefaults())
                .sessionManagement().sessionCreationPolicy((SessionCreationPolicy.STATELESS))
                .and()
                    .authorizeRequests()
                        .mvcMatchers("/api/v1/members",
                                "/api/v1/member/login",
                                "/api/v1/member/username/*/exists",
                                "/api/v1/members/email/*/duplication",
                                "/oauth2/authorization/*",
                                "/login/oauth2/code/*",
                                "/login/oauth2/*",
                                "/member/login/oauth2/code/*",
                                "/css/**", "/images/**", "/js/**", "/favicon.ico",
                                "/ws/**",
                                "/swagger-ui/**", "/v3/api-docs/**"
                        ).permitAll()
                        .mvcMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .mvcMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                .and()
                    .addFilterAfter(jsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class)
                    .addFilterBefore(jwtAuthenticationFilter(), JsonUsernamePasswordAuthenticationFilter.class)
                    .exceptionHandling()
                    .accessDeniedHandler(this::handleAccessDenied)
                    .authenticationEntryPoint(this::handleAuthenticationException)
                .and()
                    .logout()
                        .logoutUrl("/api/v1/member/logout")
                        .logoutSuccessHandler(logoutSuccessHandler());
//                .and()
//                    .oauth2Login()
//                            .redirectionEndpoint(redirection -> redirection
//                                            .baseUri("/member/login/oauth2/code/*"))
//                            .successHandler(oAuth2LoginSuccessHandler)
//                            .failureHandler(oAuth2LoginFailureHandler)
//                            .userInfoEndpoint().userService(oAuth2UserService);
        return http.build();
    }

    @Bean
    public CustomLogoutSuccessHandler logoutSuccessHandler() {
        return new CustomLogoutSuccessHandler(jwtProvider);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, memberRepository);
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtProvider, memberRepository);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter() {
        JsonUsernamePasswordAuthenticationFilter filter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(loginSuccessHandler());
        filter.setAuthenticationFailureHandler(loginFailureHandler());
        return filter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(memberSecurityService);
        return new ProviderManager(provider);
    }

    private void handleAccessDenied(HttpServletRequest request,
                                    HttpServletResponse response,
                                    AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(403);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write("권한이 없는 사용자입니다.");
    }

    private void handleAuthenticationException
            (HttpServletRequest request,
             HttpServletResponse response,
             AuthenticationException authException) throws IOException {
        response.setStatus(401);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=UTF-8");
        ErrorResult result = ErrorResult.builder()
                .code("UNAUTHORIZED")
                .message("인증되지 않은 사용자입니다.")
                .build();
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "POST", "GET", "DELETE", "PUT"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
