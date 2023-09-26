package com.example.ali.repository;

import com.example.ali.entity.RefreshToken;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUsername(String usernameFromToken);

    // refreshToken 값으로 RefreshToken을 조회하는 메서드
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
