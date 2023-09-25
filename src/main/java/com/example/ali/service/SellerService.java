package com.example.ali.service;

import com.example.ali.dto.MessageDataResponseDto;
import com.example.ali.dto.MessageResponseDto;
import com.example.ali.dto.SellerSignupRequestDto;
import com.example.ali.dto.StoreRequestDto;
import com.example.ali.dto.StoreResponseDto;
import com.example.ali.entity.Seller;
import com.example.ali.entity.SellerWallet;
import com.example.ali.entity.User;
import com.example.ali.repository.SellerRepository;

import com.example.ali.repository.SellerWalletRepository;
import com.example.ali.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final SellerWalletRepository sellerWalletRepository;
    private final PasswordEncoder passwordEncoder;;

    @Transactional
    public MessageResponseDto signup(SellerSignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String storeName = requestDto.getStoreName();
        String info = requestDto.getInfo();


        // username 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        Optional<Seller> checkSellername = sellerRepository.findByUsername(username);
        if (checkUsername.isPresent()||checkSellername.isPresent()) {
            throw new IllegalArgumentException("ID가 중복입니다.");
        }

        // storeName 중복 확인
        Optional<Seller> checkStoreName = sellerRepository.findByStoreName(storeName);
        if (checkStoreName.isPresent()) {
            throw new IllegalArgumentException("셀러명이 중복입니다.");
        }


        // 셀러 지갑 등록
        SellerWallet sellerWallet = new SellerWallet();
        sellerWalletRepository.save(sellerWallet);

        // 셀러 등록
        Seller seller = new Seller(requestDto, password, sellerWallet);
        sellerRepository.save(seller);

        return new MessageResponseDto("회원가입 성공");
//        return new ResponseEntity<>(new MessageResponseDto("회원가입 성공"), null, HttpStatus.OK);

    }

    // 상품 등록한 모든 셀러의 정보 조회
    @Transactional(readOnly = true)
    public List<StoreResponseDto> getStores() {
        List<Seller> storeList = sellerRepository.findAll();
        return storeList.stream().map(StoreResponseDto::new).toList();
    }

        @Transactional
        public MessageDataResponseDto updateStore(StoreRequestDto requestDto, Seller seller) {

            Seller store = sellerRepository.findById(seller.getId()).orElseThrow(
                () -> new NullPointerException("해당 셀러 유저가 없습니다.")
            );
            store.update(requestDto);
            return new MessageDataResponseDto("스토어 수정 성공", new StoreResponseDto(store));
        }

    @Transactional
    public MessageResponseDto deleteStore(Seller seller) {
        // 셀러는 회원가입과 동시에 Store 생성이기에
        sellerRepository.delete(seller);
        return new MessageResponseDto("Store 삭제 성공");
    }
}
