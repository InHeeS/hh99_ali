package com.example.ali.repository;

import com.example.ali.entity.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {
}