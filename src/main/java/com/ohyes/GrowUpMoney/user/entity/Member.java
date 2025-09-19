package com.ohyes.GrowUpMoney.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private Integer point_balance;

    @Column(nullable = false)
    private int is_active;

    @Column(nullable = false)
    private String tier;

    @Column(nullable = false)
    private Date created_at;

    @Column(nullable = false)
    private Date updated_at;

    public void setUsername(String username) {
        if (username.length()<= 3 ){
            throw new IllegalArgumentException();
        }
        this.username = username;
    }


}
