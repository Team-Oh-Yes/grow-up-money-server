package com.ohyes.GrowUpMoney.domain.shop.repository;

import com.ohyes.GrowUpMoney.domain.shop.entity.MemberItem;
import com.ohyes.GrowUpMoney.domain.shop.entity.ShopItem;
import com.ohyes.GrowUpMoney.domain.shop.entity.enums.ItemType;
import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberItemRepository extends JpaRepository<MemberItem, Long> {

    List<MemberItem> findByMember(Member member);

    @Query("SELECT mi FROM MemberItem mi JOIN FETCH mi.item WHERE mi.member = :member")
    List<MemberItem> findByMemberWithItem(@Param("member") Member member);

    boolean existsByMemberAndItem(Member member, ShopItem item);

    Optional<MemberItem> findByMemberAndItem(Member member, ShopItem item);

    @Query("SELECT mi FROM MemberItem mi JOIN FETCH mi.item WHERE mi.member = :member AND mi.item.itemType = :itemType AND mi.isEquipped = true")
    Optional<MemberItem> findEquippedItemByMemberAndType(@Param("member") Member member, @Param("itemType") ItemType itemType);

    @Query("SELECT mi FROM MemberItem mi JOIN FETCH mi.item WHERE mi.member = :member AND mi.isEquipped = true")
    List<MemberItem> findEquippedItemsByMember(@Param("member") Member member);
}