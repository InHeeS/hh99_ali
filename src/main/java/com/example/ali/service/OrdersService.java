package com.example.ali.service;

import com.example.ali.dto.MessageDataResponseDto;
import com.example.ali.dto.OrderRequestDto;
import com.example.ali.dto.OrderStatusRequestDto;
import com.example.ali.dto.OrdersResponseDto;
import com.example.ali.entity.*;
import com.example.ali.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersService {
    private final SellerRepository sellerRepository;
    private final OrdersRepository ordersRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;



    public MessageDataResponseDto getReview(Long orderId) {
        Orders orders = ordersRepository.findById(orderId).orElseThrow(() ->
            new IllegalArgumentException("해당 주문이 없습니다."));

        return new MessageDataResponseDto("리뷰 조회 성공", orders.getReview());
    }

    // 상품 주문
    @Transactional
    public MessageDataResponseDto orderProduct(OrderRequestDto orderRequestDto, User user) {
        Product product = findProduct(orderRequestDto.getProductId());
        ProductStock productStock = product.getProductStock();
        User realUser = findUser(user.getId());
        //구매 금액 선언
        Long totalPrice = orderRequestDto.getQnt() * product.getPrice();
        // 재고 확인
        if(!(product.getProductStock().getStock() > orderRequestDto.getQnt())) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        // 유저 소지금 확인
        if(!(user.getUserWallet().getPoint() > totalPrice)) {
            throw new IllegalArgumentException("소지금이 부족합니다.");
        }
        // 재고 변경
        productStock.changeStock(orderRequestDto.getQnt());
        // 소지금 차감
        realUser.getUserWallet().changePoint(totalPrice);

        Orders orders = new Orders(orderRequestDto, user, product);
        OrdersResponseDto ordersResponseDto = new OrdersResponseDto(orders);
        // 주문 생성
        ordersRepository.save(orders);
        return new MessageDataResponseDto("주문 성공", ordersResponseDto);
    }

    // 유저 주문 조회
    @Transactional(readOnly = true)
    public MessageDataResponseDto getUserOrders(User user) {
        // 해당 주문상품의 deleted_at null 인값만 가져오기

        return new MessageDataResponseDto("주문 조회 성공", ordersRepository.findAllByUser(user).stream().map(OrdersResponseDto::new).toList());
    }


    // 셀러 주문 조회
    @Transactional(readOnly = true)
    public MessageDataResponseDto getSellerOrders(Seller seller) {
        return new MessageDataResponseDto("주문 조회 성공", ordersRepository.findByProductSellerUsername(seller.getUsername()).stream().map(OrdersResponseDto::new).toList());
    }


    // 배송 상태 변경
    @Transactional
    public MessageDataResponseDto changeDeliveryStatus(Long ordersId, OrderStatusRequestDto orderStatusRequestDto, Seller seller) {
        Orders orders = findOrders(ordersId);
        // 셀러 확인
        if(!orders.getProduct().getSeller().getUsername().equals(seller.getUsername())) {
            throw new IllegalArgumentException("해당 주문을 변경할 수 없습니다.");
        }
        // 배송 상태 변경
        orders.changeDeliveryStatus(orderStatusRequestDto.getStatus());
        return new MessageDataResponseDto("배송 상태 변경 성공", new OrdersResponseDto(orders));
    }

    // 주문 찾기
    private Orders findOrders(Long id) {
        return ordersRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다.")
        );
    }


    // 상품 찾기
    private Product findProduct(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다.")
        );
    }

    // 유저 정보 찾기
    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다.")
        );
    }

    // 셀러 정보 찾기
    private Seller findSeller(Long id) {
        return sellerRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 셀러를 찾을 수 없습니다.")
        );
    }

}
