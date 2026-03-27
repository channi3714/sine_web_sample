// 경로: backend/src/main/java/com/sine/web/controller/AuthController.java

package com.sine.web.controller;

import com.sine.web.dto.AuthResponse;
import com.sine.web.dto.LoginRequest;
import com.sine.web.dto.RegisterRequest;
import com.sine.web.dto.UserResponse;
import com.sine.web.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 인증 관련 API 엔드포인트
 * SecurityConfig에서 /api/auth/** 는 인증 없이 접근 허용으로 설정됨
 *
 * @RestController : @Controller + @ResponseBody → 메서드 반환값을 JSON으로 응답
 * @RequestMapping : 이 컨트롤러의 모든 엔드포인트 공통 경로 접두사
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * POST /api/auth/register - 회원가입
     *
     * @Valid : RegisterRequest의 @NotBlank, @Email 등 검증 실행
     *          검증 실패 시 자동으로 400 Bad Request 응답
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserResponse response = userService.register(request);
            // 201 Created: 새 리소스가 생성됨을 의미하는 HTTP 상태코드
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            // 이메일 중복 등 비즈니스 규칙 위반
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * POST /api/auth/login - 로그인
     * 성공 시 JWT 토큰 반환
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            // 401 Unauthorized: 인증 실패
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
