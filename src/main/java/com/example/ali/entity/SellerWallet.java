package com.example.ali.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerWallet extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column
    private Long point = 0L;

    @OneToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    public void changePoint(Long totalPrice) {
        this.point += totalPrice;
    }
}
