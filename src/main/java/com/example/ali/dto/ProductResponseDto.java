package com.example.ali.dto;

import com.example.ali.entity.Product;
import com.example.ali.entity.ProductStatus;
import com.example.ali.entity.ProductStock;
import lombok.Getter;

@Getter
public class ProductResponseDto {

    private Long productId;
    private Long sellerId;
    private String productName;
    private Long price;
    private Long stock;
    private String info;
    private String imgUrl;
    private ProductStatus productStatus;

    public ProductResponseDto(Product product) {
        this.productId = product.getId();
        this.sellerId = product.getSeller().getId();
        this.productName = product.getProductName();
        this.price = product.getPrice();
        this.stock = product.getProductStock().getStock();
        this.info = product.getInfo();
        this.imgUrl = product.getImageUrl();
        this.productStatus = product.getProductStatus();
    }
}
