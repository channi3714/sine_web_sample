// 경로: backend/src/main/java/com/sine/web/dto/LoginRequest.java

package com.sine.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Java 21 Record: 로그인 요청 DTO
 */
public record LoginRequest(

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다")
        String password
) {}
