package com.ohyes.GrowUpMoney.domain.main.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MainEntity {
    @Id
    private Long id;
}
