package com.example.ali.dto;

import lombok.Getter;

@Getter
public class MessageResponseDto {
    String msg;

    public MessageResponseDto(String msg) {
        this.msg = msg;
    }
}
