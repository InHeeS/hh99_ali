package com.example.ali.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReviewRequestDto {
    @NotNull
    private Long orderId;

    @NotBlank
    private String comment;

    @NotBlank
    @Min(value = 1L) @Max(value= 5L)
    private Integer rating;

}
