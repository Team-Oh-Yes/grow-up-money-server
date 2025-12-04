package com.ohyes.GrowUpMoney.domain.shop.repository;

import com.ohyes.GrowUpMoney.domain.shop.entity.ShopItem;
import com.ohyes.GrowUpMoney.domain.shop.entity.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {

    List<ShopItem> findByIsActiveTrue();

    List<ShopItem> findByItemTypeAndIsActiveTrue(ItemType itemType);
}