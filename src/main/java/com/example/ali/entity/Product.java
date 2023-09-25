package com.example.ali.entity;

import com.example.ali.dto.ProductRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter

@Setter
@NoArgsConstructor
//@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE product SET deleted_at = CURRENT_TIMESTAMP, product_status = 'DISCONTINUED' where id = ?")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private String info;

    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(value = EnumType.STRING)
    private ProductStatus productStatus = ProductStatus.AVAILABLE;

    @Column
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private ProductStock productStock;


    public Product(ProductRequestDto requestDto, Seller seller, String imageUrl) {

        this.productName = requestDto.getProductName();
        this.price = requestDto.getPrice();
        this.info = requestDto.getInfo();
        this.imageUrl = imageUrl;
        this.seller = seller;
    }

    //test
    public Product(Long id, ProductRequestDto requestDto, Seller seller, String imageUrl) {
        this.id = id;
        this.productName = requestDto.getProductName();
        this.price = requestDto.getPrice();
        this.info = requestDto.getInfo();
        this.imageUrl = imageUrl;
        this.seller = seller;
    }

    //product 수정
    public void update(ProductRequestDto requestDto) {
        this.productName = requestDto.getProductName();
        this.price = requestDto.getPrice();
        this.info = requestDto.getInfo();
        this.productStatus = requestDto.getProductStatus();
        this.productStock.update(requestDto.getStock());
    }

}
