package com.example.ali.dto;

import com.example.ali.entity.Orders;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrdersResponseDto {
    private Long id;
    private Long qnt;
    private Long totalPrice;
    private String productName;
    private String status;
    private String productImg;
    private LocalDateTime createdAt;
    private String sellerName;
    private String username;

    public OrdersResponseDto(Orders orders) {
        this.id = orders.getId();
        this.qnt = orders.getQuantity();
        this.totalPrice = orders.getTotalPrice();
        this.productName = orders.getProduct().getProductName();
        this.status = orders.getOrderStatus().toString();
        this.productImg = orders.getProduct().getImageUrl();
        this.createdAt = orders.getCreatedAt();
        this.sellerName = orders.getProduct().getSeller().getUsername();
        this.username = orders.getUser().getUsername();
    }
}
