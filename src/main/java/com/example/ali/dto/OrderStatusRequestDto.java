package com.example.ali.dto;

import com.example.ali.entity.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderStatusRequestDto {
    private OrderStatus status;

    public OrderStatusRequestDto(OrderStatus status) {
        this.status = status;
    }
}
