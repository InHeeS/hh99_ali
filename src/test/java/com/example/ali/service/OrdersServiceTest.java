package com.example.ali.service;

import com.example.ali.dto.*;
import com.example.ali.entity.*;
import com.example.ali.repository.OrdersRepository;
import com.example.ali.repository.ProductRepository;
import com.example.ali.repository.SellerRepository;
import com.example.ali.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Mock 사용을 위해 설정
@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @InjectMocks
    private OrdersService ordersService;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;




    @Test
    @DisplayName("상품 주문 - 성공")
    void orderproduct() {

        //given

        //seller 생성
        SellerSignupRequestDto sellerSignupRequestDto =
                new SellerSignupRequestDto("셀러", "123", "셀러상점", "셀러의상점이다");
        SellerWallet sellerWallet = new SellerWallet();
        Seller seller = new Seller(1L, sellerSignupRequestDto, "123", sellerWallet);

        //user 생성
        UserSignupRequestDto userSignupRequestDto = new UserSignupRequestDto("구매자", "321", "user@user");
        UserWallet userWallet = new UserWallet();
        User user = new User(1L, userSignupRequestDto, "321", userWallet);

        //product 생성
        ProductRequestDto productRequestDto = new ProductRequestDto("축구공", 100L, 100L, "둥글함");
        Product product = new Product(1L, productRequestDto, seller, "https://pa/");
        ProductStock productStock = new ProductStock(productRequestDto.getStock(), product);
        product.setProductStock(productStock);

        //주문 생성
        OrderRequestDto orderRequestDto = new OrderRequestDto(1L, 20L);
        Orders orders = new Orders(1L, orderRequestDto, user, product);


        //when
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(ordersRepository.save(any(Orders.class))).thenReturn(orders);

        MessageDataResponseDto result = ordersService.orderProduct(orderRequestDto,user);
        OrdersResponseDto ordersResponseDto = (OrdersResponseDto) result.getData();


        //then
        Assertions.assertThat(ordersResponseDto.getQnt()).isEqualTo(20);
        Assertions.assertThat(ordersResponseDto.getTotalPrice()).isEqualTo(20*100);
        Assertions.assertThat(ordersResponseDto.getProductName()).isEqualTo("축구공");
        Assertions.assertThat(ordersResponseDto.getSellerName()).isEqualTo("셀러");
        Assertions.assertThat(ordersResponseDto.getUsername()).isEqualTo("구매자");
        Assertions.assertThat(product.getProductStock().getStock()).isEqualTo(80);
    }

    @Test
    @DisplayName("상품 주문 - 실패 - 재고 부족")
    void orderproduct2() {

        //given

        //seller 생성
        SellerSignupRequestDto sellerSignupRequestDto =
                new SellerSignupRequestDto("셀러", "123", "셀러상점", "셀러의상점이다");
        SellerWallet sellerWallet = new SellerWallet();
        Seller seller = new Seller(1L, sellerSignupRequestDto, "123", sellerWallet);

        //user 생성
        UserSignupRequestDto userSignupRequestDto = new UserSignupRequestDto("구매자", "321", "user@user");
        UserWallet userWallet = new UserWallet();
        User user = new User(1L, userSignupRequestDto, "321", userWallet);

        //product 생성
        ProductRequestDto productRequestDto = new ProductRequestDto("축구공", 100L, 100L, "둥글함");
        Product product = new Product(1L, productRequestDto, seller, "https://pa/");
        ProductStock productStock = new ProductStock(productRequestDto.getStock(), product);
        product.setProductStock(productStock);

        //주문 생성
        OrderRequestDto orderRequestDto = new OrderRequestDto(1L, 2000L);
        Orders orders = new Orders(1L, orderRequestDto, user, product);


        //when
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        Assertions.assertThatThrownBy(() -> ordersService.orderProduct(orderRequestDto, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족합니다.");
    }


    @Test
    @DisplayName("상품 주문 - 실패 - 소지금부족")
    void orderproduct3() {

        //given

        //seller 생성
        SellerSignupRequestDto sellerSignupRequestDto =
                new SellerSignupRequestDto("셀러", "123", "셀러상점", "셀러의상점이다");
        SellerWallet sellerWallet = new SellerWallet();
        Seller seller = new Seller(1L, sellerSignupRequestDto, "123", sellerWallet);

        //user 생성
        UserSignupRequestDto userSignupRequestDto = new UserSignupRequestDto("구매자", "321", "user@user");
        UserWallet userWallet = new UserWallet();
        User user = new User(1L, userSignupRequestDto, "321", userWallet);

        //product 생성
        ProductRequestDto productRequestDto = new ProductRequestDto("축구공", 10000000L, 100L, "둥글함");
        Product product = new Product(1L, productRequestDto, seller, "https://pa/");
        ProductStock productStock = new ProductStock(productRequestDto.getStock(), product);
        product.setProductStock(productStock);

        //주문 생성
        OrderRequestDto orderRequestDto = new OrderRequestDto(1L, 20L);
        Orders orders = new Orders(1L, orderRequestDto, user, product);


        //when
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        Assertions.assertThatThrownBy(() -> ordersService.orderProduct(orderRequestDto, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("소지금이 부족합니다.");
    }

}