package com.example.ali.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserWallet extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column
    private Long point = 1000000L;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    //test
    public UserWallet(Long point) {
        this.point = point;
    }

    public void changePoint(Long requestPoint) {
        this.point -= requestPoint;
    }

}
