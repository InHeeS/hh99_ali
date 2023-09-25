package com.example.ali.repository;

import com.example.ali.entity.Orders;
import com.example.ali.entity.Review;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByOrdersIn(List<Orders> orders);

    Optional<Review> findByOrders_Id(Long orderId);


}