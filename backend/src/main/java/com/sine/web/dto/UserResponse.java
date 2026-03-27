// 경로: backend/src/main/java/com/sine/web/dto/UserResponse.java

package com.sine.web.dto;

import com.sine.web.domain.User;

import java.time.LocalDateTime;

/**
 * Java 21 Record: 사용자 프로필 조회 응답 DTO
 *
 * 중요: 절대로 User 엔티티를 그대로 응답으로 내보내면 안 됨!
 * → password 같은 민감 정보가 노출될 수 있고,
 * → JPA 지연 로딩 관련 직렬화 오류가 발생할 수 있음
 * → 항상 DTO로 변환해서 필요한 정보만 응답
 */
public record UserResponse(
        Long id,
        String email,
        String username,
        LocalDateTime createdAt
) {
    // User 엔티티 → UserResponse 변환 정적 팩토리 메서드
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getCreatedAt()
        );
    }
}
