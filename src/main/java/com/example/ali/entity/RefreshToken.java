package com.example.ali.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

import static com.example.ali.jwt.JwtUtil.REFRESH_TIME;

@Getter
@ToString
@NoArgsConstructor  // 기본 생성자 추가
@AllArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = REFRESH_TIME)
public class RefreshToken {

    @Id
    private String id;

    private String refreshToken;

    @Indexed
    private String username;

    public RefreshToken(String token, String username) {
        this.refreshToken = token;
        this.username = username;
    }

    public RefreshToken updateToken(String token) {
        this.refreshToken = token;
        return this;
    }
}
