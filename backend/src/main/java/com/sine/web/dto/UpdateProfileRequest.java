// 경로: backend/src/main/java/com/sine/web/dto/UpdateProfileRequest.java

package com.sine.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProfileRequest(

        @NotBlank(message = "사용자명은 필수입니다")
        @Size(min = 2, max = 20, message = "사용자명은 2~20자 사이여야 합니다")
        String username,

        @Size(max = 200, message = "자기소개는 200자 이하여야 합니다")
        String bio,

        // @Past: 반드시 과거 날짜여야 함 (미래 생년월일 방지)
        @Past(message = "생년월일은 과거 날짜여야 합니다")
        LocalDate birthday
) {}
