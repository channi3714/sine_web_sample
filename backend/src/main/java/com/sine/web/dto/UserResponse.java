// 경로: backend/src/main/java/com/sine/web/dto/UserResponse.java

package com.sine.web.dto;

import com.sine.web.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponse(
        // id 제거: DB auto-increment PK는 외부에 노출하지 않음
        // → 순차 ID 노출 시 사용자 수 추측, IDOR 공격 등에 취약해질 수 있음
        String email,
        String username,
        String bio,
        LocalDate birthday,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getEmail(),
                user.getUsername(),
                user.getBio(),
                user.getBirthday(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
