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
                .mvcMatchers(HttpMethod.POST, "/api/v1/members").permitAll()
                .mvcMatchers(HttpMethod.GET, "/api/v1/members/email/*/duplication").permitAll()
                .mvcMatchers(HttpMethod.POST, "/api/v1/refresh/access-token").permitAll()
                .mvcMatchers(HttpMethod.GET, "/css/**").permitAll()
                .mvcMatchers(HttpMethod.GET, "/images/**").permitAll()
                .mvcMatchers(HttpMethod.GET, "/js/**").permitAll()
                .mvcMatchers(HttpMethod.GET, "/favicon.ico").permitAll()
                .mvcMatchers(HttpMethod.GET, "/ws/**").permitAll()
                .mvcMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated();
        return http.build();
    }
}