// 경로: backend/src/main/java/com/sine/web/dto/AuthResponse.java

package com.sine.web.dto;

/**
 * Java 21 Record: 로그인 성공 응답 DTO
 * 클라이언트(React)는 이 토큰을 받아서 localStorage에 저장하고,
 * 이후 모든 API 요청의 Authorization 헤더에 포함시킴
 */
public record AuthResponse(
        String token,       // JWT 토큰
        String tokenType,   // 항상 "Bearer"
        String email,
        String username
) {
    // 정적 팩토리 메서드: 호출 코드를 더 읽기 좋게 만들어줌
    public static AuthResponse of(String token, String email, String username) {
        return new AuthResponse(token, "Bearer", email, username);
    }
}
