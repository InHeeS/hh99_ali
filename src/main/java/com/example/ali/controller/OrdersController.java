package com.example.ali.controller;

import com.example.ali.dto.MessageDataResponseDto;
import com.example.ali.dto.OrderRequestDto;
import com.example.ali.dto.OrderStatusRequestDto;
import com.example.ali.dto.OrdersResponseDto;
import com.example.ali.security.SellerDetailsImpl;
import com.example.ali.security.UserDetailsImpl;
import com.example.ali.service.OrdersService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrdersController {
    private final OrdersService ordersService;

    // 해당 주문에 따른 리뷰 조회
    @Operation(summary = "해당 주문에 따른 리뷰 조회 ")
    @GetMapping("/user/order/review/{orderId}")
    public ResponseEntity<MessageDataResponseDto> getOrderReview(@PathVariable Long orderId) {
        return ResponseEntity.status(200).body(ordersService.getReview(orderId));
    }

    // 주문 확인 조회
    @Operation(summary = "주문 확인 조회")
    @GetMapping("/seller/orders")
    public ResponseEntity<MessageDataResponseDto> getSellerOrders(@AuthenticationPrincipal SellerDetailsImpl sellerDetails) {
        return ResponseEntity.status(200).body(ordersService.getSellerOrders(sellerDetails.getSeller()));
    }


    // 배송 상태 변경
    @Operation(summary = "배송 상태 변경")
    @PostMapping("/seller/orders/{ordersId}")
    public ResponseEntity<MessageDataResponseDto> changeDeliveryStatus(@PathVariable Long ordersId, @RequestBody OrderStatusRequestDto orderStatusRequestDto, @AuthenticationPrincipal SellerDetailsImpl sellerDetails) {
        return ResponseEntity.status(200).body(ordersService.changeDeliveryStatus(ordersId, orderStatusRequestDto, sellerDetails.getSeller()));

    }

    // 상품 주문
    @Operation(summary = "상품 주문")
    @PostMapping("/product/order")
    public ResponseEntity<MessageDataResponseDto> orderProduct(@RequestBody OrderRequestDto orderRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(200).body(ordersService.orderProduct(orderRequestDto, userDetails.getUser()));
    }

    // 주문 조회 (유저 본인의 주문 내역 조회)
    @Operation(summary = "주문 조회 (유저 본인의 주문 내역 조회)")
    @GetMapping("/user/orders")
    public ResponseEntity<MessageDataResponseDto> getUserOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(200).body(ordersService.getUserOrders(userDetails.getUser()));
    }


}
