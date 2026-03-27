// 경로: backend/src/main/java/com/sine/web/config/SecurityConfig.java

package com.sine.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 핵심 설정
 *
 * @Configuration : 스프링 설정 클래스 (Bean 정의 포함)
 * @EnableWebSecurity : Spring Security 활성화
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 비밀번호 암호화 방식: BCrypt
     *
     * BCrypt는 단방향 해시 함수로:
     * - "password123" → "$2a$10$xyz..." (매번 다른 결과, salt 자동 포함)
     * - 복호화 불가 → DB가 털려도 원본 비밀번호 알 수 없음
     * - 비교: passwordEncoder.matches("원본", "해시값") → true/false
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager: 로그인 시 이메일/비밀번호 검증을 담당하는 객체
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 보안 필터 체인 - Spring Security의 핵심 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF(Cross-Site Request Forgery) 비활성화
            // JWT는 stateless라서 CSRF 공격에 취약하지 않음 (세션 쿠키를 안 씀)
            .csrf(AbstractHttpConfigurer::disable)

            // CORS 설정 적용 (아래 corsConfigurationSource() Bean 사용)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 세션 정책: STATELESS
            // JWT 방식이므로 서버가 세션을 전혀 만들지 않음
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 요청별 인증/인가 규칙
            .authorizeHttpRequests(auth -> auth
                // 회원가입, 로그인은 누구나 접근 가능
                .requestMatchers("/api/auth/**").permitAll()
                // /api/users/** 는 로그인한 사용자만 접근 가능
                .requestMatchers("/api/users/**").authenticated()
                // 나머지 모든 요청도 인증 필요
                .anyRequest().authenticated()
            )

            // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 삽입
            // 모든 요청에서 JWT 토큰을 먼저 확인하도록 순서 지정
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS(Cross-Origin Resource Sharing) 설정
     *
     * 왜 CORS가 필요한가?
     * - 브라우저 보안 정책: 다른 출처(Origin)의 리소스 요청을 기본적으로 차단
     * - React(localhost:3000) → Spring Boot(localhost:8080) = 다른 Origin!
     * - 서버에서 "이 출처는 허용해줄게" 라고 응답 헤더로 알려줘야 함
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 출처 (개발 환경)
        // 운영 환경에서는 실제 도메인으로 변경할 것
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",   // React 개발 서버
                "http://localhost:5173"    // Vite 기본 포트
        ));

        // 허용할 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 허용할 요청 헤더 (Authorization: Bearer <token> 포함)
        config.setAllowedHeaders(List.of("*"));

        // 쿠키/인증 정보 포함 허용
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 경로에 적용
        return source;
    }
}
