package com.example.ali.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderRequestDto {
    private Long productId;
    private Long qnt;

    public OrderRequestDto(Long productId, Long qnt) {
        this.productId = productId;
        this.qnt = qnt;
    }
}
