// 경로: backend/src/main/java/com/sine/web/repository/UserRepository.java

package com.sine.web.repository;

import com.sine.web.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JpaRepository를 상속하면 아래 CRUD 메서드가 자동으로 생성됨:
 * - save(), findById(), findAll(), deleteById() 등
 *
 * 메서드 이름 규칙만 지키면 구현 없이 쿼리 자동 생성:
 * - findByEmail → "SELECT * FROM users WHERE email = ?"
 * - existsByEmail → "SELECT COUNT(*) > 0 FROM users WHERE email = ?"
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // 로그인 시 이메일로 사용자 조회
    // Optional: 결과가 없을 수도 있음을 명시 (null 대신 Optional.empty() 반환)
    Optional<User> findByEmail(String email);

    // 회원가입 시 이메일 중복 확인
    boolean existsByEmail(String email);
}
