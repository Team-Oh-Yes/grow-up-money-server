package com.ohyes.GrowUpMoney.domain.user.entity;

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

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer point_balance;

    @Column(nullable = false, columnDefinition = "boolean default true")

    @Column(nullable = false, columnDefinition = "VARCHAR(255) default 'user'")
    private String tier;

    @Column(nullable = false)

    @Column(nullable = false)
    private Date updated_at;

    }


}
