package com.example.ali.controller;

import com.example.ali.dto.MessageDataResponseDto;
import com.example.ali.dto.MessageResponseDto;
import com.example.ali.dto.SellerSignupRequestDto;
import com.example.ali.dto.StoreRequestDto;
import com.example.ali.dto.StoreResponseDto;
import com.example.ali.security.SellerDetailsImpl;
import com.example.ali.service.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @Operation(summary = "seller 회원가입")
    @PostMapping("/auth/seller/signup")
    public ResponseEntity<MessageResponseDto> signup(@RequestBody SellerSignupRequestDto requestDto){
        return ResponseEntity.ok(sellerService.signup(requestDto));
//        return sellerService.signup(requestDto);
    }
    @Operation(summary = "store 정보 전체 조회")
    @GetMapping("/api/seller/store")
    public ResponseEntity<List<StoreResponseDto>> getStores(){

        return ResponseEntity.ok(sellerService.getStores());
    }

    @Operation(summary = "store 수정")
    @PutMapping("/api/seller/store")
    public ResponseEntity<MessageDataResponseDto> updateStore(@RequestBody StoreRequestDto requestDto,
                                                              @AuthenticationPrincipal SellerDetailsImpl sellerDetails){
        return ResponseEntity.ok(sellerService.updateStore(requestDto, sellerDetails.getSeller()));

    }

    @Operation(summary = "store 삭제")
    @DeleteMapping("/api/seller/store")
    public ResponseEntity<MessageResponseDto> deleteStore(@AuthenticationPrincipal SellerDetailsImpl sellerDetails){

        return ResponseEntity.ok(sellerService.deleteStore(sellerDetails.getSeller()));
    }

    // (뷰) 회원가입 페이지 이동
    @GetMapping("/auth/seller/signup-page")
    public String signupPage() {
        return "sellerSignup";
    }

}
