// 경로: backend/src/main/java/com/sine/web/domain/User.java

package com.sine.web.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User 엔티티 - DB의 'users' 테이블과 1:1 매핑되는 클래스
 *
 * @Entity    : JPA가 이 클래스를 DB 테이블로 관리하게 함
 * @Table     : 테이블 이름을 명시적으로 지정 (기본값은 클래스명 소문자)
 * @Getter    : Lombok - 모든 필드의 getter 자동 생성
 * @NoArgsConstructor : JPA는 리플렉션으로 객체를 만들기 때문에 기본 생성자가 반드시 필요
 *              PROTECTED로 설정해 외부에서 new User()를 막고, Builder를 통해서만 생성하도록 강제
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    /**
     * @Id            : 기본키(PK) 지정
     * @GeneratedValue: AUTO_INCREMENT 방식으로 DB가 ID를 자동 생성
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column: 컬럼 속성 지정
     * - unique = true  : 이메일 중복 불가 (DB 레벨 제약조건)
     * - nullable = false: NOT NULL 제약조건
     * - length          : VARCHAR 길이 지정
     */
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String username;

    // 비밀번호는 반드시 BCrypt 해시값으로 저장 (평문 저장 절대 금지!)
    @Column(nullable = false)
    private String password;

    // Java 8+ LocalDateTime: 생성 시각 자동 기록
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // @PrePersist: 엔티티가 DB에 처음 저장되기 직전에 자동 실행되는 메서드
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Builder 패턴: 필드가 많을 때 생성자보다 가독성이 좋음
     * 사용법: User.builder().email("...").username("...").password("...").build()
     */
    @Builder
    private User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
