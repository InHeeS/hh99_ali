package com.example.ali.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SellerSignupRequestDto {
    private String username;
    private String password;
    private String storeName;
    private String info;

    public SellerSignupRequestDto(String username, String password, String storeName, String info) {
        this.username = username;
        this.password = password;
        this.storeName = storeName;
        this.info = info;
    }
}
