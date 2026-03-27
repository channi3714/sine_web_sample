// 경로: backend/src/main/java/com/sine/web/config/JwtAuthenticationFilter.java

package com.sine.web.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터
 *
 * 모든 HTTP 요청이 컨트롤러에 도달하기 전에 이 필터를 거침
 * 흐름:
 * 1. 요청 헤더에서 "Authorization: Bearer <token>" 추출
 * 2. 토큰 유효성 검증
 * 3. 유효하면 SecurityContext에 인증 정보 저장
 * 4. 이후 컨트롤러에서 @AuthenticationPrincipal 등으로 사용자 정보 접근 가능
 *
 * OncePerRequestFilter: 한 요청에 딱 한 번만 실행되는 필터 (중복 실행 방지)
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Authorization 헤더에서 토큰 추출
        String token = extractTokenFromRequest(request);

        // 2. 토큰이 있고 유효하면 인증 처리
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {

            // 3. 토큰에서 이메일 추출 후 DB에서 사용자 조회
            String email = jwtUtil.extractEmail(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 4. SecurityContext에 인증 정보 저장
            //    → 이후 컨트롤러에서 "이 요청은 인증된 사용자"로 인식
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. 다음 필터로 요청 전달 (필터 체인 계속 진행)
        filterChain.doFilter(request, response);
    }

    /**
     * "Authorization: Bearer eyJhb..." 헤더에서 토큰 부분만 추출
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후 문자열
        }
        return null;
    }
}
