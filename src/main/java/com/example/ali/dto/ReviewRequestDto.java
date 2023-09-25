package com.example.ali.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {
    private Long orderId;

    @NotBlank
    private String comment;

    @NotBlank
//    @Min(value = 1L) @Max(value= 5L)
    private Integer rating;

    public ReviewRequestDto(Long orderId, String comment, Integer rating) {
        this.orderId = orderId;
        this.comment = comment;
        this.rating = rating;
    }
}
