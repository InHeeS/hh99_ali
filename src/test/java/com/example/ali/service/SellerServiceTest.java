package com.example.ali.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.example.ali.dto.MessageDataResponseDto;
import com.example.ali.dto.MessageResponseDto;
import com.example.ali.dto.SellerSignupRequestDto;
import com.example.ali.dto.StoreRequestDto;
import com.example.ali.dto.StoreResponseDto;
import com.example.ali.entity.Seller;
import com.example.ali.entity.SellerWallet;
import com.example.ali.repository.SellerRepository;
import com.example.ali.repository.SellerWalletRepository;
import com.example.ali.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @InjectMocks // 의존성을 필요로 하는 field 에 붙이는 애노테이션으로, @Mock 이나 @Spy 애노테이션이 붙은 field 를 주입받는다.
    private SellerService sellerService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private SellerWalletRepository sellerWalletRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

//    @BeforeEach
//    public void setUp(){
//        MockitoAnnotations.openMocks(this);
//    }

    @Test
    @DisplayName("셀러의 회원가입 성공")
    @Transactional
    public void signup(){

        // given
        SellerSignupRequestDto requestDto = new SellerSignupRequestDto("testSeller", "1234",
            "storeName", "storeInfo");
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        SellerWallet sellerWallet = new SellerWallet();
        Seller seller = new Seller(requestDto, password, sellerWallet);

        // when
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn(password);

        when(userRepository.findByUsername("testSeller")).thenReturn(Optional.empty());
        when(sellerRepository.findByUsername("testSeller")).thenReturn(Optional.empty());
        when(sellerRepository.findByStoreName("storeName")).thenReturn(Optional.empty());

        when(sellerWalletRepository.save(any(SellerWallet.class))).thenReturn(sellerWallet);
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        // 테스트 실행
        MessageResponseDto messageResponseDto = sellerService.signup(requestDto);


        // then
        Assertions.assertNotNull(messageResponseDto.getMsg());
    }

    @Test
    @DisplayName("상품 등록한 모든 셀러의 정보 조회 성공")
    @Transactional(readOnly = true)
    public void getStores(){
        //given
        List<Seller> storeList = new ArrayList<>();
        storeList.add(new Seller("Store1", "Info1"));
        storeList.add(new Seller("Store2", "Info2"));

        //when
        when(sellerRepository.findAll()).thenReturn(storeList);

        List<StoreResponseDto> storeResponseDtos = sellerService.getStores();

        //then

        assertEquals(2, storeResponseDtos.size()); // 2개의 셀러 정보가 있어야 함
        assertEquals("Store1", storeResponseDtos.get(0).getStoreName());
        assertEquals("Info1", storeResponseDtos.get(0).getInfo());
        assertEquals("Store2", storeResponseDtos.get(1).getStoreName());
        assertEquals("Info2", storeResponseDtos.get(1).getInfo());
    }

    @Test
    @DisplayName("해당 셀러 상점 수정하기 성공")
    @Transactional
    public void updateStore(){
        // given
        StoreRequestDto requestDto = new StoreRequestDto();
//        requestDto.setSellerId(1L);
        requestDto.setInfo("Info1 update");
        requestDto.setStoreName("Store1 update");


        Seller seller = new Seller();
//        Seller seller = new Seller("Store1", "Info1");
        seller.setId(1L);

        // when
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        MessageDataResponseDto messageDataResponseDto = sellerService.updateStore(requestDto, seller);

        // then
        StoreResponseDto updatedStore = (StoreResponseDto) messageDataResponseDto.getData();

        assertEquals(seller.getStoreName(), "Store1 update");
        assertEquals("Store1 update", updatedStore.getStoreName());
        assertEquals("Info1 update", updatedStore.getInfo());

    }

    @Test
    @DisplayName("해당 셀러 상점 삭제하기 성공")
    @Transactional
    public void deleteStore(){

        // given
        Seller seller = new Seller("Store1", "Info1");
        seller.setId(1L); // 가짜 셀러 ID 설정

        // when
        doNothing().when(sellerRepository).delete(seller);
        MessageResponseDto messageResponseDto = sellerService.deleteStore(seller);
        // then
        Assertions.assertNotNull(messageResponseDto.getMsg());
        // 반환 메세지 검증은 생략
    }


}