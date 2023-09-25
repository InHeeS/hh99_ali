package com.example.ali.repository;

import com.example.ali.entity.Seller;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByUsername(String username);
    Optional<Seller> findByStoreName(String storeName);
}