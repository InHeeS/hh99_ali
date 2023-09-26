package com.example.ali.service;

import com.example.ali.dto.MessageResponseDto;
import com.example.ali.dto.ReviewRequestDto;
import com.example.ali.dto.ReviewResponseDto;
import com.example.ali.entity.Orders;
import com.example.ali.entity.Product;
import com.example.ali.entity.Review;
import com.example.ali.entity.User;
import com.example.ali.repository.OrdersRepository;
import com.example.ali.repository.ProductRepository;
import com.example.ali.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrdersRepository ordersRepository;
    private final ProductRepository productRepository;

    public List<ReviewResponseDto> getProductReviewList(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다"));
        
        // Order_id로 조인하고 그 결과를가져오기
        List<Orders> orders = ordersRepository.findAllByProduct(product);
        List<Review> productReviews = reviewRepository.findAllByOrdersIn(orders);

        return productReviews.stream()
                        .map(ReviewResponseDto::new)
                        .toList();
    }

    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto requestDto, User user) {
        Orders order = ordersRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new NullPointerException("해당하는 주문이 존재하지 않습니다"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("작성 권한이 없는 유저 입니다.");
        }

        Optional<Review> originReview = reviewRepository.findByOrders_Id(requestDto.getOrderId());
        if (originReview.isPresent()) {
            throw new IllegalArgumentException("이미 작성한 리뷰가 있습니다");
        }

        Review review = reviewRepository.save(new Review(requestDto, order));
        return new ReviewResponseDto(review);
    }

    @Transactional
    public ReviewResponseDto updateReview(ReviewRequestDto requestDto, User user) {
        Review review = reviewRepository.findByOrders_Id(requestDto.getOrderId())
                .orElseThrow(() -> new NullPointerException("해당하는 리뷰가 존재하지 않습니다"));

        if (!(review.getOrders().getUser().getId().equals(user.getId()))) {
            throw new IllegalArgumentException("작성 권한이 없는 유저 입니다.");
        }

        review.update(requestDto);
        return new ReviewResponseDto(review);
    }

    @Transactional
    public MessageResponseDto deleteReview(Long orderId, User user) {
        Review review = reviewRepository.findByOrders_Id(orderId)
                .orElseThrow(() -> new NullPointerException("해당하는 리뷰가 존재하지 않습니다"));

        if (!(review.getOrders().getUser().getId().equals(user.getId()))) {
            throw new IllegalArgumentException("삭제 권한이 없는 유저 입니다.");
        }

        reviewRepository.delete(review);
        return new MessageResponseDto("삭제 성공");
    }

}
