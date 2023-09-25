package com.example.ali.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    private String username;
    private String password;
    private String userType; // 새로운 필드: "SELLER" 또는 "USER" 값을 가질 수 있습니다.
}