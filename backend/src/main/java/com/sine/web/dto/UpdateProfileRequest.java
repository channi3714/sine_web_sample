// 경로: backend/src/main/java/com/sine/web/dto/UpdateProfileRequest.java

package com.sine.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @NotBlank(message = "사용자명은 필수입니다")
        @Size(min = 2, max = 20, message = "사용자명은 2~20자 사이여야 합니다")
        String username,

        // bio는 선택 입력 (null 허용)
        @Size(max = 200, message = "자기소개는 200자 이하여야 합니다")
        String bio
) {}
