package com.sign.global.security.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .headers().disable()
                .authorizeRequests()
                .mvcMatchers("/api/v1/members",
                        "/api/v1/member/login",
                        "/api/v1/member/username/*/exists",
                        "/api/v1/members/email/*/duplication",
                        "/oauth2/authorization/*",
                        "/login/oauth2/code/*",
                        "/css/**", "/images/**", "/js/**", "/favicon.ico",
                        "/ws/**",
                        "/swagger-ui/**", "/v3/api-docs/**"
                ).permitAll()
                .mvcMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated();
        return http.build();
    }
}