// 경로: backend/src/main/java/com/sine/web/service/UserService.java

package com.sine.web.service;

import com.sine.web.config.JwtUtil;
import com.sine.web.domain.User;
import com.sine.web.dto.AuthResponse;
import com.sine.web.dto.LoginRequest;
import com.sine.web.dto.RegisterRequest;
import com.sine.web.dto.UpdateProfileRequest;
import com.sine.web.dto.UserResponse;
import com.sine.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 비즈니스 로직 담당 서비스
 *
 * Controller → Service → Repository 흐름:
 * - Controller: HTTP 요청/응답만 처리 (얇게 유지)
 * - Service: 실제 비즈니스 규칙 처리 (이 파일)
 * - Repository: DB 접근만 담당
 *
 * 이렇게 분리하면: 테스트하기 쉽고, 각 레이어의 역할이 명확해짐
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 (성능 최적화)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     * @Transactional : 이 메서드는 데이터를 변경하므로 쓰기 트랜잭션 필요
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // 1. 이메일 중복 확인
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2. 비밀번호 BCrypt 암호화
        //    "password123" → "$2a$10$..." (단방향, 복호화 불가)
        String encodedPassword = passwordEncoder.encode(request.password());

        // 3. User 엔티티 생성 및 저장
        User user = User.builder()
                .email(request.email())
                .username(request.username())
                .password(encodedPassword)  // 반드시 암호화된 비밀번호 저장!
                .build();

        User savedUser = userRepository.save(user);

        // 4. 응답 DTO로 변환 (비밀번호 필드 제외됨)
        return UserResponse.from(savedUser);
    }

    /**
     * 로그인 - JWT 토큰 발급
     */
    public AuthResponse login(LoginRequest request) {
        // 1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다."));

        // 2. 비밀번호 검증
        //    입력한 평문 비밀번호 vs DB에 저장된 BCrypt 해시값 비교
        //    matches()가 내부적으로 salt를 추출해서 비교함
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            // 보안: 이메일/비밀번호 중 어느 것이 틀렸는지 알려주지 않음
            throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 3. JWT 토큰 발급
        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.of(token, user.getEmail(), user.getUsername());
    }

    /**
     * 내 프로필 조회
     * @param email 현재 로그인한 사용자 이메일 (JWT에서 추출)
     */
    public UserResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.updateProfile(request.username(), request.bio());

        // @Transactional 덕분에 save() 호출 없이도 변경사항이 자동으로 DB에 반영됨
        // (JPA 더티 체킹: 트랜잭션 종료 시 변경된 엔티티를 감지해서 UPDATE 쿼리 실행)
        return UserResponse.from(user);
    }
}
