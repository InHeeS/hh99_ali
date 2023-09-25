package com.example.ali.controller;

import com.example.ali.dto.ReviewRequestDto;
import com.example.ali.dto.ReviewResponseDto;
import com.example.ali.service.ReviewService;
import com.example.ali.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/api/product/{productId}/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getProductReviews(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviewList(productId));
    }

    @PostMapping("/api/user/review")
    public ResponseEntity<ReviewResponseDto> createReview(@RequestBody ReviewRequestDto requestDto,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(reviewService.createReview(requestDto, userDetails.getUser()));
    }

    @PutMapping("/api/review")
    public ResponseEntity<ReviewResponseDto> updateReview(@RequestBody ReviewRequestDto requestDto,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.accepted()
                .body(reviewService.updateReview(requestDto, userDetails.getUser()));
    }

    @DeleteMapping("/api/review/{orderId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long orderId,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(reviewService.deleteReview(orderId, userDetails.getUser()));
    }
}
