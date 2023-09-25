package com.example.ali.controller;

import com.example.ali.dto.MessageDataResponseDto;
import com.example.ali.dto.ProductRequestDto;
import com.example.ali.dto.ProductResponseDto;
import com.example.ali.entity.Seller;
import com.example.ali.security.SellerDetailsImpl;
import com.example.ali.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    //seller 상품 등록
    @Operation(summary = "seller 상품 등록")
    @PostMapping("/seller/product")
    public ResponseEntity<MessageDataResponseDto> createProduct(
            @AuthenticationPrincipal SellerDetailsImpl sellerDetails,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "requestDto", required = false) ProductRequestDto requestDto) throws IOException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProduct(requestDto, sellerDetails.getSeller(), image));
    }

    //seller 상품 수정
    @Operation(summary = "seller 상품 수정")
    @PutMapping("/seller/product/{productId}")
    public ResponseEntity<MessageDataResponseDto> updateProduct(@PathVariable Long productId,
                                           @RequestBody ProductRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(productService.updateProduct(productId, requestDto));
    }

    //seller 상품 삭제
    @Operation(summary = "seller 상품 삭제")
    @DeleteMapping("/seller/product/{productId}")
    public ResponseEntity<MessageDataResponseDto> deleteProduct(@PathVariable Long productId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.deleteProduct(productId));
    }

    //전체, 검색 상품 조회
    @Operation(summary = "선택 상품 조회")
    @GetMapping("/seller/products")
    public ResponseEntity<List<ProductResponseDto>> getSearchProduct(@RequestParam String keyword) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getSearchProduct(keyword));
    }

    @Operation(summary = "전체 상품 조회")
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDto>> getProducts() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getProducts());
    }

    @Operation(summary = "셀러 본인의 상품 조회")
    @GetMapping("/products/seller")
    public ResponseEntity<List<ProductResponseDto>> getProducts(@AuthenticationPrincipal SellerDetailsImpl sellerDetails) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getSellerProducts(sellerDetails.getSeller()));
    }
}
