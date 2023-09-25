package com.example.ali.entity;

import com.example.ali.dto.SellerSignupRequestDto;
import com.example.ali.dto.StoreRequestDto;
import com.example.ali.dto.UserSignupRequestDto;
import com.example.ali.repository.SellerWalletRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter // test
@NoArgsConstructor
@SQLDelete(sql = "UPDATE seller SET deleted_at = CURRENT_TIMESTAMP where id = ?")
@Where(clause = "deleted_at IS NULL")
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false)
    private String info;

    @Column
    private LocalDateTime deletedAt;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "seller")
    private SellerWallet sellerWallet;

    public Seller(SellerSignupRequestDto requestDto, String password, SellerWallet sellerWallet) {
        this.username = requestDto.getUsername();
        this.password = password;
        this.storeName = requestDto.getStoreName();
        this.info = requestDto.getInfo();
        this.sellerWallet = sellerWallet;
        sellerWallet.setSeller(this);
    }

    //test
    public Seller(String username) {
        this.username = username;
    }

    //test
    public Seller(String storeName, String info) {
        this.storeName = storeName;
        this.info = info;
    }

    //test
    public Seller(String username, String password, String storeName, String info,
        SellerWallet sellerWallet) {
        this.username = username;
        this.password = password;
        this.storeName = storeName;
        this.info = info;
        this.sellerWallet =sellerWallet;
        sellerWallet.setSeller(this);
    }
    public Seller(Long id, SellerSignupRequestDto requestDto, String password, SellerWallet sellerWallet) {
        this.id = id;
        this.username = requestDto.getUsername();
        this.password = password;
        this.storeName = requestDto.getStoreName();
        this.info = requestDto.getInfo();
        this.sellerWallet = sellerWallet;
        sellerWallet.setSeller(this);
    }

    public Seller(String username, String password, String storeName, String info) {
        this.username = username;
        this.password = password;
        this.storeName = storeName;
        this.info = info;
    }

    public void update(StoreRequestDto requestDto) {
        this.storeName = requestDto.getStoreName();
        this.info = requestDto.getInfo();
    }

    //test
    public void update(String storeName, String info) {
        this.storeName = storeName;
        this.info = info;
    }
}
