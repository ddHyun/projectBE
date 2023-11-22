package org.koreait.api.controllers.members;

/*
    회원가입 데이터를 받을 클래스
 */

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RequestJoin(
        @NotBlank @Email
        String email,
        @NotBlank @Size(min=8)
        String password,
        @NotBlank
        String confirmPassword,
        @NotBlank
        String name,
        String mobile,
        @AssertTrue
        Boolean agree) {//기본형은 가끔 오류가 나니 래퍼클래스로 적기
}
