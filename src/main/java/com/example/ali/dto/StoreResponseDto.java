package com.example.ali.dto;

import com.example.ali.entity.Seller;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter // test
public class StoreResponseDto {
    private Long sellerId;
    private String storeName;
    private String info;

    public StoreResponseDto(Seller seller) {
        this.sellerId = seller.getId();
        this.storeName = seller.getStoreName();
        this.info = seller.getInfo();
    }
}
