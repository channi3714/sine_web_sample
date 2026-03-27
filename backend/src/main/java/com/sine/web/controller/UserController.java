// 경로: backend/src/main/java/com/sine/web/controller/UserController.java

package com.sine.web.controller;

import com.sine.web.dto.UserResponse;
import com.sine.web.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 정보 관련 API 엔드포인트
 * SecurityConfig에서 /api/users/** 는 인증된 사용자만 접근 가능으로 설정됨
 * → JWT 토큰 없이 요청하면 자동으로 401 Unauthorized 반환
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users/me - 내 프로필 조회
     *
     * @AuthenticationPrincipal : JwtAuthenticationFilter가 SecurityContext에 저장한
     *                            인증 정보(UserDetails)를 자동 주입받음
     *                            → 토큰에서 이메일을 꺼내는 과정이 자동화됨
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // userDetails.getUsername()은 우리 구현에서 이메일을 반환함
        // (CustomUserDetailsService에서 .username(user.getEmail())로 설정했기 때문)
        UserResponse response = userService.getMyProfile(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
