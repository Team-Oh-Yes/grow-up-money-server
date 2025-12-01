package com.ohyes.GrowUpMoney.domain.shop.entity;

import com.ohyes.GrowUpMoney.domain.shop.entity.enums.ItemType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tb_shop_item")
public class ShopItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType itemType;

    @Column(nullable = false)
    private Integer price;

    @Column
    private String imageUrl;

    @Column
    private String itemValue;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public ShopItem(String name, String description, ItemType itemType, Integer price) {
        this.name = name;
        this.description = description;
        this.itemType = itemType;
        this.price = price;
        this.isActive = true;
    }

}
