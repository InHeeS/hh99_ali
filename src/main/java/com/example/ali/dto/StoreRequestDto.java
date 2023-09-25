package com.example.ali.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter // test
public class StoreRequestDto {
    private String storeName;
    private String info;

    public StoreRequestDto(String storeName, String info) {
        this.storeName = storeName;
        this.info = info;
    }

    public StoreRequestDto() {

    }
}
