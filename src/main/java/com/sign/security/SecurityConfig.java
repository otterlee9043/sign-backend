package com.sign.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sign.domain.member.controller.dto.AuthErrorResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final MemberSecurityService memberSecurityService;
    private final JwtProvider jwtProvider;

    private final OAuth2UserService oAuth2UserService;

    private ObjectMapper objectMapper = new ObjectMapper();

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .antMatchers("/**")
//                .permitAll();
//        http.csrf().disable();
//        http
//                .formLogin()
//                .loginPage("/api/member/login").defaultSuccessUrl("/");
//        http
//                .logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/api/member/logout"))
//                .invalidateHttpSession(true);
//        return http.build();
//    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
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
                            "/login/oauth2/code/*"
                            ).permitAll()
                    .mvcMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .accessDeniedHandler(this::handleAccessDenied)
                .authenticationEntryPoint(this::handleAuthenticationException)
                .and()
                .oauth2Login()
                    .userInfoEndpoint()
                        .userService(oAuth2UserService);
        return http.build();
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
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }

    //    private void configureCors(CorsConfigurer c) {
//        CorsConfigurationSource source = request -> {
//            CorsConfiguration config = new CorsConfiguration();
//            config.setAllowedOrigins(
//                    List.of("http://localhost:3000/")
//            );
//            config.setAllowedMethods(
//                    List.of("*")
//            );
//            return config;
//        };
//        c.configurationSource(source);
//    }
}