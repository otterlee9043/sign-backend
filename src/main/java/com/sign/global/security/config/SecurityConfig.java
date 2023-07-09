package com.sign.global.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sign.domain.member.controller.dto.AuthErrorResult;
import com.sign.domain.member.repository.MemberRepository;
import com.sign.global.security.authentication.JwtProvider;
import com.sign.global.security.authentication.MemberSecurityService;
import com.sign.global.security.authentication.OAuth2MemberService;
import com.sign.global.security.filter.JsonUsernamePasswordAuthenticationFilter;
import com.sign.global.security.filter.JwtAuthenticationFilter;
import com.sign.global.security.handler.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

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

    private ObjectMapper objectMapper = new ObjectMapper();


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                //                .cors(withDefaults())
//                .cors(this::configureCors)
                .sessionManagement().sessionCreationPolicy((SessionCreationPolicy.STATELESS))
                .and()
                .authorizeRequests()
                    .mvcMatchers("/api/member/join",
                            "/api/member/login",
                            "/api/member/username/*/exists",
                            "/api/member/email/*/exists",
                            "/oauth2/authorization/*",
                            "/login/oauth2/code/*",
                            "/css/**","/images/**","/js/**","/favicon.ico",
                            "/swagger-ui/**", "/v3/api-docs/**"
                            ).permitAll()
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
                        .logoutUrl("/api/member/logout")
                        .logoutSuccessHandler(logoutSuccessHandler())
                .and()
                .oauth2Login()
                    .successHandler(oAuth2LoginSuccessHandler)
                    .failureHandler(oAuth2LoginFailureHandler)
                    .userInfoEndpoint().userService(oAuth2UserService);
        return http.build();
    }

    @Bean
    public CustomLogoutSuccessHandler logoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
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

    private void handleAccessDenied(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(403);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write("권한이 없는 사용자입니다.");
    }

    private void handleAuthenticationException
            (HttpServletRequest request,
             HttpServletResponse response,
             AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(401);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=UTF-8");
        AuthErrorResult result = AuthErrorResult.builder().message("인증되지 않은 사용자입니다.").build();
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("HEAD","POST","GET","DELETE","PUT"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
