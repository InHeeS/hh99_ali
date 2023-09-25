package com.example.ali.dto;

import com.example.ali.entity.ProductStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductRequestDto {
    private String productName;
    private Long price;
    private Long stock;
    private String info;
    private ProductStatus productStatus;

    public ProductRequestDto(String productName, Long price, Long stock, String info, ProductStatus productStatus) {
        this.productName = productName;
        this.price = price;
        this.stock = stock;
        this.info = info;
        this.productStatus = productStatus;
    }

    public ProductRequestDto(String name, Long price, Long stock, String info) {
        this.productName = name;
        this.price = price;
        this.stock = stock;
        this.info = info;
    }
}

