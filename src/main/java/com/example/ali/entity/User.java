package com.example.ali.entity;

import com.example.ali.dto.UserSignupRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter // test
@NoArgsConstructor
public class User extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserWallet userWallet;

    public User(UserSignupRequestDto requestDto, String password, UserWallet userWallet) {
        this.username = requestDto.getUsername();
        this.email = requestDto.getEmail();
        this.password = password;
        this.userWallet = userWallet;
        userWallet.setUser(this);
    }

    //test
    public User(Long id, UserSignupRequestDto requestDto, String password, UserWallet userWallet) {
        this.id = id;
        this.username = requestDto.getUsername();
        this.email = requestDto.getEmail();
        this.password = password;
        this.userWallet = userWallet;
        userWallet.setUser(this);
    }


    public User(String user, String number, String mail, UserWallet userWallet) {
        this.username = user;
        this.password = number;
        this.email = mail;
        this.userWallet = userWallet;
        userWallet.setUser(this);
    }
}
