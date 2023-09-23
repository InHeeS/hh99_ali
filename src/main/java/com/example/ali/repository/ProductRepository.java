package com.example.ali.repository;

import com.example.ali.entity.Product;
import com.example.ali.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByProductNameLike(String keyword);

    List<Product> findAllBySeller(Seller seller);
}