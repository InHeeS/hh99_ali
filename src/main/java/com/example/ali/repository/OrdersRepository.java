package com.example.ali.repository;

import com.example.ali.entity.Orders;
import com.example.ali.entity.Product;
import com.example.ali.entity.Seller;
import com.example.ali.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findAllByUser(User user);
    List<Orders> findByProductSellerUsername(String username);
    List<Orders> findAllByProduct(Product product);
}