// 경로: backend/src/main/java/com/sine/web/domain/User.java

package com.sine.web.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    // 자기소개 (선택 입력)
    @Column(length = 200)
    private String bio;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    private User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    // 프로필 수정 메서드 - 엔티티 내부에서만 필드 변경 가능하도록
    public void updateProfile(String username, String bio) {
        this.username = username;
        this.bio = bio;
    }
}
