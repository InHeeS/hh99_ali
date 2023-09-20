package com.example.ali.controller;

import com.example.ali.dto.ReviewRequestDto;
import com.example.ali.service.ReviewService;
import com.example.ali.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    @GetMapping("/api/product/{productId}/reviews")
    public ResponseEntity<?> getProductReviews(
            @PathVariable("productId") Long productId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok()
                .body(reviewService.getProductReviewList(productId));
    }

    @PostMapping("/api/user/review")
    public ResponseEntity<?> createReview(
            @RequestBody ReviewRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(requestDto, userDetails.getUser()));
    }

    @PutMapping("/api/review/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(reviewService.updateReview(requestDto, reviewId, userDetails.getUser()));
    }

    @DeleteMapping("/api/review/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok()
                .body(reviewService.deleteReview(reviewId, userDetails.getUser()));
    }
}
