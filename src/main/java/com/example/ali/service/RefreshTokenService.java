package com.example.ali.service;

import com.example.ali.entity.RefreshToken;
import com.example.ali.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public String getRefreshTokenByUserName(String username) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUsername(username);
        return optionalRefreshToken.map(RefreshToken::getRefreshToken).orElse(null);
    }
}
