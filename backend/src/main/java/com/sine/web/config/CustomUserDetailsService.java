// 경로: backend/src/main/java/com/sine/web/config/CustomUserDetailsService.java

package com.sine.web.config;

import com.sine.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security가 인증할 때 사용하는 사용자 조회 서비스
 *
 * Spring Security는 내부적으로 UserDetailsService를 통해 사용자를 DB에서 가져옴
 * 우리가 이 인터페이스를 구현해서 "이메일로 조회"하도록 커스터마이징
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 이메일로 사용자를 DB에서 조회
     * Spring Security의 User 객체로 변환해서 반환
     *
     * @param email 로그인 시 입력한 이메일
     * @throws UsernameNotFoundException 해당 이메일 사용자가 없을 때
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.sine.web.domain.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // Spring Security의 내장 User 객체로 변환
        // 세 번째 인자는 권한(Role) 목록 - 지금은 기본 권한만
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())  // BCrypt 해시값
                .roles("USER")
                .build();
    }
}
