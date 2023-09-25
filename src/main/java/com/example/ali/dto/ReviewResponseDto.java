package com.example.ali.dto;

import com.example.ali.entity.Review;
import lombok.Getter;

@Getter
public class ReviewResponseDto {

    private Long id;
    private String comment;
    private Integer rating;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.comment = review.getComment();
        this.rating = review.getRating();
    }
}

