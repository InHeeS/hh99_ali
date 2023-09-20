package com.example.ali.controller;

import com.example.ali.dto.UserWalletResponseDto;
import com.example.ali.security.UserDetailsImpl;
import com.example.ali.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WalletController {
    private final UserService userservice;
    @GetMapping("/api/user/wallet")
    public ResponseEntity<UserWalletResponseDto> getUserPoint(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok().body(userservice.getUserPoint(userDetails.getUser()));
    }
}
