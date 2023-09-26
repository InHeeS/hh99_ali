package com.example.ali.service;

import com.example.ali.dto.*;
import com.example.ali.entity.*;
import com.example.ali.repository.OrdersRepository;
import com.example.ali.repository.ProductRepository;
import com.example.ali.repository.SellerRepository;
import com.example.ali.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {
    @InjectMocks
    OrdersService ordersService;

    @Mock
    OrdersRepository ordersRepository;

    @Mock
    ProductRepository productRepository;
    @Mock
    SellerRepository sellerRepository;
    @Mock
    UserRepository userRepository;
    
    @Nested
    @DisplayName("상품 주문")
    class SuccessCase {
        @Mock User user;
        @Mock UserWallet userWallet;
        @Mock Seller seller;
        @Mock SellerWallet sellerWallet;

        @Mock Product product;
        @Mock ProductStock productStock;

        @Test
        void 주문_성공() {
            //given
            Long productPrice = 1000L;
            Long stock = 1000L;
            Long userPoint = 100000L;
            Long qnt = 20L;

            OrderRequestDto requestDto = new OrderRequestDto(1L, qnt);
            UserWallet wallet = new UserWallet(userPoint);

            //when
            //Product 관련
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
            when(product.getProductStock()).thenReturn(productStock);
            when(productStock.getStock()).thenReturn(stock);
            when(product.getPrice()).thenReturn(productPrice);
            when(product.getSeller()).thenReturn(seller);
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

            //User 관련
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(user.getUserWallet()).thenReturn(wallet);

            //then
            MessageDataResponseDto result = ordersService.orderProduct(requestDto, user);
            OrdersResponseDto resultDto =(OrdersResponseDto) result.getData();

            verify(productStock, times(1)).changeStock(anyLong());
            verify(ordersRepository, times(1)).save(any(Orders.class));

            assertThat(resultDto.getQnt()).isEqualTo(qnt);
            assertThat(resultDto.getTotalPrice()).isEqualTo(Math.multiplyExact(productPrice,qnt));
            assertThat(wallet.getPoint()).isEqualTo(Math.subtractExact(userPoint, Math.multiplyExact(productPrice,qnt)));
        }
        @Test
        void 주문_실패_재고부족() {
            Long stock = 100L;
            Long qnt = 1000L;
            OrderRequestDto requestDto = new OrderRequestDto(1L, qnt);

            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
            when(product.getProductStock()).thenReturn(productStock);
            when(productStock.getStock()).thenReturn(stock);
            //user 관련
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> ordersService.orderProduct(requestDto, user))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("재고가 부족합니다.");
            verify(productStock, times(0)).changeStock(anyLong());
        }

        @Test
        void 주문_실패_소지금부족() {
            //given
            Long price = 1000L;
            Long stock = 100L;

            Long point = 100L;
            Long qnt = 1L;
            OrderRequestDto requestDto = new OrderRequestDto(1L, qnt);

            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
            when(product.getProductStock()).thenReturn(productStock);
            when(product.getPrice()).thenReturn(price);
            when(productStock.getStock()).thenReturn(stock);

            // user
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(user.getUserWallet()).thenReturn(userWallet);
            when(userWallet.getPoint()).thenReturn(point);

            //then
            assertThatThrownBy(() -> ordersService.orderProduct(requestDto, user))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("소지금이 부족합니다.");
            verify(userWallet, times(0)).changePoint(anyLong());
        }
    }
}