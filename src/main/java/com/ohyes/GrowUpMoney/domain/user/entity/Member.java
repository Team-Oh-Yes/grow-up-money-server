package com.ohyes.GrowUpMoney.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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
    private Boolean is_active = true;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) default 'user'")
    private String tier;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @Column(nullable = false)
    private LocalDateTime updated_at;


    @PrePersist
    public void prePersist() {
        if (this.is_active == null) this.is_active = true;
        if (this.point_balance == null) this.point_balance = 0;
        if (this.tier == null) this.tier = "user";
        LocalDateTime now = LocalDateTime.now();
        this.created_at = now;
        this.updated_at = now;
    }


}
