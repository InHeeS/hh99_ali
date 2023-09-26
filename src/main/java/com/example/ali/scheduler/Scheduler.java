package com.example.ali.scheduler;

import com.example.ali.entity.Orders;
import com.example.ali.entity.Review;
import com.example.ali.entity.User;
import com.example.ali.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j(topic = "Scheduler")
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final UserRepository userRepository;
    private final OrdersRepository ordersRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    @Scheduled(cron = "0 0 * * * *") // 1시간 모든 유저의 modified_at 체크
    public void signEscape() {

        List<User> userList = userRepository.findAll();
        for (User user : userList) {

            Duration duration = Duration.between(user.getUserWallet().getUpdatedAt(), LocalDateTime.now());
            long time = duration.getSeconds();

            if (time > 600000L) {

                List<Orders> ordersList = ordersRepository.findAllByUser(user);

                for (Orders orders : ordersList) {

                    Optional<Review> review = reviewRepository.findByOrders_Id(orders.getId());
                    if (review.isPresent()) {
                        reviewRepository.delete(review.get());
                    } else {
                        ordersRepository.delete(orders);
                    }
                }
                userRepository.delete(user);
            }
        }

    }
}
