// 경로: backend/src/main/java/com/sine/web/config/JwtUtil.java

package com.sine.web.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT(JSON Web Token) 유틸리티 클래스
 *
 * JWT 구조: Header.Payload.Signature
 * - Header  : 알고리즘 정보 (HS256 등)
 * - Payload : 사용자 정보(Claims) - 이메일, 만료시간 등 (base64 인코딩, 암호화 아님!)
 * - Signature: Header + Payload를 비밀키로 서명 → 위변조 감지
 *
 * 왜 JWT를 세션 대신 쓰나요?
 * - 세션: 서버 메모리에 상태 저장 → 서버가 여러 대면 공유 어려움
 * - JWT: 토큰 자체에 정보 포함 → 서버는 비밀키만 있으면 검증 가능 (Stateless)
 * - 보안 학습 관점: JWT의 구조와 취약점(알고리즘 변조 등)을 이해하기 좋음
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expiration;

    // application.yml의 jwt.secret 값을 주입받음
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ) {
        // 비밀키는 최소 256비트(32바이트) 이상이어야 HS256 알고리즘 사용 가능
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * JWT 토큰 생성
     * @param email 사용자 이메일 (토큰의 subject로 저장)
     * @return 서명된 JWT 문자열
     */
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(email)          // 토큰 주인 (이메일)
                .issuedAt(now)           // 발급 시각
                .expiration(expiryDate)  // 만료 시각
                .signWith(secretKey)     // 비밀키로 서명 (HS256)
                .compact();
    }

    /**
     * 토큰에서 이메일(subject) 추출
     */
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 토큰 유효성 검증
     * - 서명 일치 여부
     * - 만료 여부
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 위변조되거나 만료된 토큰은 여기서 걸림
            return false;
        }
    }

    // 토큰 파싱 (서명 검증 포함)
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
