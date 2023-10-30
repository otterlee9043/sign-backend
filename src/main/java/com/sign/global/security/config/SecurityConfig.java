package com.sign.global.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sign.global.exception.ErrorResult;
import com.sign.global.security.authentication.jwt.JwtProvider;
import com.sign.global.security.authentication.DefaultUserDetailsService;
import com.sign.global.security.authentication.oauth2.OAuth2LoginService;
import com.sign.global.security.filter.CustomOAuth2AuthorizationRequestRedirectFilter;
import com.sign.global.security.filter.CustomOAuth2LoginAuthenticationFilter;
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
import org.springframework.web.client.RestTemplate;
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

    private final JwtProvider jwtProvider;

    private final OAuth2LoginService oAuth2LoginService;

    private final DefaultUserDetailsService defaultUserDetailsService;


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
                        .mvcMatchers(HttpMethod.POST, "/api/v1/members").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/api/v1/members/email/*/duplication").permitAll()
                        .mvcMatchers(HttpMethod.POST, "/api/v1/refresh/access-token").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/css/**").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/images/**").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/js/**").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/favicon.ico").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/ws/**").permitAll()
                        .mvcMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                .and()
                    .addFilterBefore(jwtAuthenticationFilter(), LogoutFilter.class)
                    .addFilterBefore(jsonUsernamePasswordAuthenticationFilter(), JwtAuthenticationFilter.class)
                    .addFilterBefore(customOAuth2LoginAuthenticationFilter(), JwtAuthenticationFilter.class)
                    .addFilterBefore(customOAuth2AuthorizationRequestRedirectFilter(), JwtAuthenticationFilter.class)
                    .exceptionHandling()
                        .accessDeniedHandler(this::handleAccessDenied)
                        .authenticationEntryPoint(this::handleAuthenticationException)
                .and()
                    .logout()
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler());
        return http.build();
    }

    @Bean
    public CustomLogoutSuccessHandler logoutSuccessHandler() {
        return new CustomLogoutSuccessHandler(jwtProvider);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider);
    }

    @Bean
    public CustomOAuth2AuthorizationRequestRedirectFilter customOAuth2AuthorizationRequestRedirectFilter() {
        return new CustomOAuth2AuthorizationRequestRedirectFilter(oAuth2LoginService);
    }
    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtProvider);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter() {
        JsonUsernamePasswordAuthenticationFilter filter = new JsonUsernamePasswordAuthenticationFilter(objectMapper());
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(loginSuccessHandler());
        filter.setAuthenticationFailureHandler(loginFailureHandler());
        return filter;
    }

    @Bean
    public CustomOAuth2LoginAuthenticationFilter customOAuth2LoginAuthenticationFilter() {
        CustomOAuth2LoginAuthenticationFilter filter
                = new CustomOAuth2LoginAuthenticationFilter(oAuth2LoginService);
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(loginSuccessHandler());
        filter.setAuthenticationFailureHandler(loginFailureHandler());
        return filter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(defaultUserDetailsService);
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
        response.getWriter().write(objectMapper().writeValueAsString(result));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

        @Bean
        public ObjectMapper objectMapper() { return new ObjectMapper(); }

        @Bean
        public RestTemplate restTemplate() { return new RestTemplate(); }

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
