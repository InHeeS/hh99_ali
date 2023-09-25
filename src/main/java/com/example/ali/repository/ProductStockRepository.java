package com.example.ali.repository;

import com.example.ali.entity.Product;
import com.example.ali.entity.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
    ProductStock findProductStockByProduct(Product product);
}