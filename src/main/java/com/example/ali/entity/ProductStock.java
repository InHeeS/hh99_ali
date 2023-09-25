package com.example.ali.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class ProductStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column
    private Long stock;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductStock(Long stock, Product product) {
        this.stock = stock;
        this.product = product;
    }

    public void update(Long stock) {
        this.stock = stock;
    }

    public void changeStock(Long requestStock) {
        stock -= requestStock;
    }
}
