package com.example.ali.entity;

public enum ProductStatus {
    // 판매중
    AVAILABLE,
    // 일시품절
    OUT_OF_STOCK, // product 재고(Stock) 0이 되었을 때
    // 품절
    DISCONTINUED; // seller가 product 데이터를 지울 때
}
