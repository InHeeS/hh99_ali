package com.example.ali.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ali.dto.MessageResponseDto;
import com.example.ali.dto.UserSignupRequestDto;
import com.example.ali.dto.UserWalletResponseDto;
import com.example.ali.entity.User;
import com.example.ali.entity.UserWallet;
import com.example.ali.repository.SellerRepository;
import com.example.ali.repository.UserRepository;
import com.example.ali.repository.UserWalletRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class) // SpringContainer 를 로드하지 않는다.
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private UserWalletRepository userWalletRepository;

    // Stub : 기대행위를 작성하여 테스트에서 원하는 상황을 설정하게 한다. (기대 행위를 작성하는것)
    // Spy : Stub 하지 않은 메소드는 실제 메소드로 동작, 실제 사용자 비밀번호를 암호화 해야됨
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    // 필드에 대한 Mock 객체들을 초기화
    // ullPointerException 또는 다른 예외 발생 예방

//    @BeforeEach
//    public void setup() {
//    }

    @Test
    @DisplayName("유저 회원가입 성공")
    @Transactional
    void signup() {
        // 준비 - 실행 - 검증
        //given
        UserSignupRequestDto requestDto = new UserSignupRequestDto("testUser", "1234", "test@email.com");
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        UserWallet userWallet = new UserWallet(); // 빈 객체 가능
        User user = new User(requestDto, password, userWallet);

        //when
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn(password);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(sellerRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        when(userWalletRepository.save(any(UserWallet.class))).thenReturn(userWallet); // any : 어떤 인자가 호출 되더라도 동작
        when(userRepository.save(any(User.class))).thenReturn(user);

//        ResponseEntity<?> responseEntity = userService.signup(requestDto);
        MessageResponseDto messageResponseDto = userService.signup(requestDto);

        //then

        verify(userRepository, times(1)).findByUsername("testUser");
        verify(sellerRepository, times(1)).findByUsername("testUser");

        verify(userWalletRepository, times(1)).save(any(UserWallet.class));
        verify(userRepository, times(1)).save(any(User.class));

        Assertions.assertNotNull(messageResponseDto);

    }

    @Test
    @DisplayName("유저의 포인트 조회 성공")
    void getUserPoint() {

        //given
        UserWallet userWallet = new UserWallet(1000L);
        userWallet.setId(1L);

        User user = new User("user", "1234", "user@naver.com", userWallet);
        user.setId(1L);

        // when
        UserWalletResponseDto userWalletResponseDto = userService.getUserPoint(user);

        //then
        // userService is not a mock -> verify(x)
        assertThat(userWalletResponseDto.getPoint()).isEqualTo(1000);

    }

    // custom 함수 따로 빼어 써도 된다. 어노테이션 불필요
}