package com.example.ali.controller;

import com.example.ali.dto.MessageResponseDto;
import com.example.ali.dto.UserSignupRequestDto;
import com.example.ali.security.SellerDetailsImpl;
import com.example.ali.security.UserDetailsImpl;
import com.example.ali.service.MailService;
import com.example.ali.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth/user")
public class UserController {

    private final UserService userService;
    private final MailService mailService;

    @Operation(summary = "user 회원가입")
    @PostMapping("/signup")
    public ResponseEntity<MessageResponseDto> signup(@RequestBody UserSignupRequestDto requestDto){
        return ResponseEntity.ok(userService.signup(requestDto));
    }





    @ResponseBody // form 을 통해 입력, 뷰 구현 필요
    @PostMapping("/email")
    public String MailSend(String mail){

        int number = mailService.sendMail(mail);
        String num = "" + number;

        return num;
    }

    // (뷰) 로그인 페이지 이동
    @GetMapping("/login-page")
    public String loginPage() {
        return "login";
    }

    // (뷰) 회원가입 페이지 이동
    @GetMapping("/signup-page")
    public String signupPage() {
        return "signup";
    }

}
