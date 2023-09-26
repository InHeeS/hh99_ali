package com.example.ali.entity;

import com.example.ali.dto.ReviewRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;


@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE review SET deleted_at = CURRENT_TIMESTAMP where id = ?")
@Where(clause = "deleted_at IS NULL")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false, updatable = false)
    private Integer rating;

    @Column
    private LocalDateTime deletedAt;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "review", cascade = CascadeType.ALL)
    private Orders orders;

    public Review(ReviewRequestDto requestDto, Orders orders) {
        this.comment = requestDto.getComment();
        this.rating = requestDto.getRating();
        this.orders = orders;
        orders.setReview(this);
    }

    public void update(ReviewRequestDto requestDto) {
        this.comment = requestDto.getComment();
        this.rating = requestDto.getRating();
    }
}
