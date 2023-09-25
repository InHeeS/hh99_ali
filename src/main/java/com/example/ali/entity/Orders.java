package com.example.ali.entity;

import com.example.ali.dto.OrderRequestDto;
import com.example.ali.dto.OrderStatusRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Orders extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long totalPrice;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToOne
    @JoinColumn(name = "review_id")
    private Review review;

    public Orders(OrderRequestDto orderRequestDto, User user, Product product) {
        this.totalPrice = product.getPrice() * orderRequestDto.getQnt();
        this.quantity = orderRequestDto.getQnt();
        this.orderStatus = OrderStatus.DELIVERING;
        this.user = user;
        this.product = product;
    }

    //test
    public Orders(Long id, OrderRequestDto orderRequestDto, User user, Product product) {
        this.id = id;
        this.totalPrice = product.getPrice() * orderRequestDto.getQnt();
        this.quantity = orderRequestDto.getQnt();
        this.orderStatus = OrderStatus.DELIVERING;
        this.user = user;
        this.product = product;
    }

    public void changeDeliveryStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }


}
