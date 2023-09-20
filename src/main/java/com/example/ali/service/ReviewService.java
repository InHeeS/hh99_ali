//package com.example.ali.service;
//
//import com.example.ali.dto.MessageResponseDto;
//import com.example.ali.dto.ReviewRequestDto;
//import com.example.ali.dto.ReviewResponseDto;
//import com.example.ali.entity.Orders;
//import com.example.ali.entity.Product;
//import com.example.ali.entity.Review;
//import com.example.ali.entity.User;
//import com.example.ali.repository.OrdersRepository;
//import com.example.ali.repository.ProductRepository;
//import com.example.ali.repository.ReviewRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ReviewService {
//
//    private final ReviewRepository reviewRepository;
//    private final OrdersRepository ordersRepository;
//    private final ProductRepository productRepository;
//
//    public ResponseEntity<?> getProductReviewList(Long productId) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다"));
//        // Order_id로 조인하고 그 결과를 가져오기
//        List<Orders> orders = ordersRepository.findAllByProduct(product);
//        List<Review> productReviews = reviewRepository.findAllWithOrdersByOrders(orders);
//
//        return ResponseEntity.ok(
//                productReviews.stream()
//                        .map(ReviewResponseDto::new)
//                        .toList()
//        );
//    }
//
//    @Transactional
//    public ResponseEntity<?> createReview(ReviewRequestDto requestDto, User user) {
//        Orders order = ordersRepository.findById(requestDto.getOrderId())
//                .orElseThrow(() -> new NullPointerException("해당하는 상품이 존재하지 않습니다"));
//
//        if (!order.getUser().equals(user)) {
//            throw new IllegalArgumentException("작성 권한이 없는 유저 입니다.");
//        }
//
//        Review review = new Review(requestDto,order);
//        return ResponseEntity
//                .status(HttpStatus.CREATED.value())
//                .body(new ReviewResponseDto(reviewRepository.save(review)));
//    }
//
//    @Transactional
//    public ResponseEntity<?> updateReview(ReviewRequestDto requestDto, Long reviewId, User user) {
//        Review review = reviewRepository.findById(reviewId)
//                .orElseThrow(() -> new NullPointerException("해당하는 리뷰가 존재하지 않습니다"));
//        if (!review.getOrders().getUser().equals(user)) {
//            throw new IllegalArgumentException("작성 권한이 없는 유저 입니다.");
//        }
//        review.update(requestDto);
//        return ResponseEntity.accepted().body(new ReviewResponseDto(review));
//    }
//
//    @Transactional
//    public ResponseEntity<?> deleteReview(Long reviewId, User user) {
//        Review review = reviewRepository.findById(reviewId)
//                .orElseThrow(() -> new NullPointerException("해당하는 리뷰가 존재하지 않습니다"));
//
//        if (!review.getOrders().getUser().equals(user)) {
//            throw new IllegalArgumentException("삭제 권한이 없는 유저 입니다.");
//        }
//
//        reviewRepository.delete(review);
//        return ResponseEntity.ok(new MessageResponseDto("삭제 성공"));
//    }
//
//}
