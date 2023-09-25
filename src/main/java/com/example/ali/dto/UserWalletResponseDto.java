package com.example.ali.dto;

import com.example.ali.entity.UserWallet;
import lombok.Getter;

@Getter
public class UserWalletResponseDto {
    private final Long point;
    public UserWalletResponseDto(UserWallet wallet) {
        this.point = wallet.getPoint();
    }
}
