package com.example.ali.repository;


import static org.assertj.core.api.Assertions.*;

import com.example.ali.entity.User;
import com.example.ali.entity.UserWallet;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest //  @Tranasactional 을 가지고 있기 때문에 테스트가 진행된 이후 자동으로 롤백하는 것이 가능
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 Database를 이용해서 테스트
class UserRepositoryTest {
    // crud + custom
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserWalletRepository userWalletRepository;

    @Test
    @DisplayName("유저 및 유저 지갑 생성 테스트")
    void saveUser(){

        // given
        UserWallet userWallet = new UserWallet(1000L);
        userWalletRepository.save(userWallet);
        User user = new User("user1" , "1234","user1@naver.com", userWallet);

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(savedUser.getUserWallet().getPoint()).isEqualTo(userWallet.getPoint());
    }

    @Test
    @DisplayName("유저 리스트 조회 테스트")
    void findListUser(){

        // given
        List<User> users1 =  userRepository.findAll();
        UserWallet userWallet = new UserWallet(1000L);
        userWalletRepository.save(userWallet);
        User user = new User("user1" , "1234","user1@naver.com", userWallet);

        UserWallet userWallet2 = new UserWallet(2000L);
        userWalletRepository.save(userWallet2);
        User user2 = new User("user1" , "1234","user1@naver.com", userWallet2);

        // when
        userRepository.save(user);
        userRepository.save(user2);

        // when
        List<User> users =  userRepository.findAll();
        // then
        assertThat(users).isNotNull().hasSize(users1.size()+2); // 위의 생성 test 2번 실행후
    }


}