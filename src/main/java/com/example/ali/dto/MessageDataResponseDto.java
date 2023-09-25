package com.example.ali.dto;

import lombok.Getter;

@Getter
public class MessageDataResponseDto {
    private String msg;
    private Object data;

    public MessageDataResponseDto(String msg, Object data) {
        this.msg = msg;
        this.data = data;
    }
}
