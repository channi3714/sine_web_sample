// 경로: backend/src/main/java/com/sine/web/dto/UserResponse.java

package com.sine.web.dto;

import com.sine.web.domain.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        String username,
        String bio,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getBio(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
