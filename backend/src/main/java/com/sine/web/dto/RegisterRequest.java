// 경로: backend/src/main/java/com/sine/web/dto/RegisterRequest.java

package com.sine.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Java 21 Record: 회원가입 요청 DTO
 *
 * Record는 Java 16에서 정식 도입된 기능으로,
 * - 모든 필드는 자동으로 private final
 * - 생성자, getter, equals, hashCode, toString 자동 생성
 * - DTO처럼 "데이터만 담는 객체"에 최적화
 *
 * 기존 방식 대비: @Getter + @AllArgsConstructor + class 선언이 한 줄로 끝남
 */
public record RegisterRequest(

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        @NotBlank(message = "사용자명은 필수입니다")
        @Size(min = 2, max = 20, message = "사용자명은 2~20자 사이여야 합니다")
        String username,

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
        String password
) {}
