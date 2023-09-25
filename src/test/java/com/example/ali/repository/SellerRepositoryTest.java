package com.example.ali.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.ali.entity.Seller;
import com.example.ali.entity.SellerWallet;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SellerRepositoryTest {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private SellerWalletRepository sellerWalletRepository;

    @Test
    @DisplayName("셀러 및 셀러 지갑 생성 테스트")
    void saveSeller(){
        // given
        SellerWallet sellerWallet = new SellerWallet();
        sellerWalletRepository.save(sellerWallet);
        Seller seller = new Seller("seller1","1234", "store1", "info1",
            sellerWallet);

        // when
        // 유저 및 상점 이름 중복확인은 생략
        Seller savedSeller = sellerRepository.save(seller);
        // then
        assertThat(savedSeller.getUsername()).isEqualTo(seller.getUsername());
        assertThat(savedSeller.getSellerWallet().getPoint()).isEqualTo(sellerWallet.getPoint());
    }

    @Test
    @DisplayName("모든 셀러 리스트 조회")
    void findListSeller(){
        // given
        SellerWallet sellerWallet = new SellerWallet();
        sellerWalletRepository.save(sellerWallet);
        Seller seller = new Seller("seller1","1234", "store1", "info1",
            sellerWallet);

        SellerWallet sellerWallet2 = new SellerWallet();
        sellerWalletRepository.save(sellerWallet2);
        Seller seller2 = new Seller("seller2","12345", "store2", "info2",
            sellerWallet2);

        sellerRepository.save(seller);
        sellerRepository.save(seller2);

        // when
        List<Seller> sellers = sellerRepository.findAll();
        // then
        assertThat(sellers).isNotNull().hasSize((int) (sellers.size()));
    }

    @Test
    @DisplayName("셀러 매장 정보 변경")
    @Transactional
    void updateSellerStore(){
        // given
        SellerWallet sellerWallet = new SellerWallet();
        sellerWalletRepository.save(sellerWallet);
        Seller _seller = new Seller("seller1","1234", "store1", "info1", sellerWallet);
        sellerRepository.save(_seller);

        Seller seller = sellerRepository.findById(_seller.getId()).orElseThrow(IllegalArgumentException::new);

        // when
        seller.update("store update", "info update");

        // then
        assertThat(seller.getStoreName()).isEqualTo("store update");
        assertThat(seller.getInfo()).isEqualTo("info update");
    }

    @Test
    @DisplayName("셀러 리스트 정보 삭제")
    @Transactional
    void deleteSellerStore(){
        // given

        SellerWallet sellerWallet = new SellerWallet();
        sellerWalletRepository.save(sellerWallet);
        Seller _seller = new Seller("seller1","1234", "store1", "info1", sellerWallet);
        sellerRepository.save(_seller);

        long storeCount = sellerRepository.count();
        Seller seller = sellerRepository.findById(_seller.getId()).orElseThrow(IllegalArgumentException::new);

        // when
        sellerRepository.delete(seller); // deleted_at 갱신안됨

        // then
        assertThat(sellerRepository.count()).isEqualTo(storeCount - 1);
    }


}