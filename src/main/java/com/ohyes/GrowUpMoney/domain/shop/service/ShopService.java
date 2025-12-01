package com.ohyes.GrowUpMoney.domain.shop.service;

import com.ohyes.GrowUpMoney.domain.shop.dto.request.PointExchangeRequest;
import com.ohyes.GrowUpMoney.domain.shop.dto.response.*;
import com.ohyes.GrowUpMoney.domain.shop.entity.MemberItem;
import com.ohyes.GrowUpMoney.domain.shop.entity.ShopItem;
import com.ohyes.GrowUpMoney.domain.shop.entity.enums.ItemType;
import com.ohyes.GrowUpMoney.domain.shop.exception.AlreadyOwnedItemException;
import com.ohyes.GrowUpMoney.domain.shop.exception.InsufficientBoundPointException;
import com.ohyes.GrowUpMoney.domain.shop.exception.ItemNotFoundException;
import com.ohyes.GrowUpMoney.domain.shop.repository.MemberItemRepository;
import com.ohyes.GrowUpMoney.domain.shop.repository.ShopItemRepository;
import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.exception.UserNotFoundException;
import com.ohyes.GrowUpMoney.domain.auth.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {

    private final ShopItemRepository shopItemRepository;
    private final MemberItemRepository memberItemRepository;
    private final MemberRepository memberRepository;

    // 아이템 목록 조회
    public List<ShopItemResponse> getItems() {
        return shopItemRepository.findByIsActiveTrue()
                .stream()
                .map(ShopItemResponse::from)
                .collect(Collectors.toList());
    }

    // 타입별 아이템 목록 조회
    public List<ShopItemResponse> getItemsByType(ItemType itemType) {
        return shopItemRepository.findByItemTypeAndIsActiveTrue(itemType)
                .stream()
                .map(ShopItemResponse::from)
                .collect(Collectors.toList());
    }

    // 아이템 구매
    @Transactional
    public ItemPurchaseResponse purchaseItem(String username, Long itemId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        ShopItem item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        // 이미 보유한 아이템인지 확인
        if (memberItemRepository.existsByMemberAndItem(member, item)) {
            throw new AlreadyOwnedItemException();
        }

        // 귀속 포인트 확인
        if (member.getBoundPoint() < item.getPrice()) {
            throw new InsufficientBoundPointException(item.getPrice(), member.getBoundPoint());
        }

        // 귀속 포인트 차감
        member.setBoundPoint(member.getBoundPoint() - item.getPrice());
        memberRepository.save(member);

        // 아이템 지급
        MemberItem memberItem = new MemberItem(member, item);
        memberItemRepository.save(memberItem);

        log.info("아이템 구매 완료: 유저={}, 아이템={}, 가격={}", username, item.getName(), item.getPrice());

        return ItemPurchaseResponse.from(memberItem, member.getBoundPoint());
    }

    // 재화 전환 (귀속 포인트 → 거래 가능 포인트)
    @Transactional
    public PointExchangeResponse exchangePoints(String username, PointExchangeRequest request) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        Integer amount = request.getAmount();

        // 귀속 포인트 확인
        if (member.getBoundPoint() < amount) {
            throw new InsufficientBoundPointException(amount, member.getBoundPoint());
        }

        // 전환 수행
        member.convertBoundPointToPoint(amount);
        memberRepository.save(member);

        log.info("포인트 전환 완료: 유저={}, 전환량={}", username, amount);

        return new PointExchangeResponse(
                "포인트 전환이 완료되었습니다.",
                amount,
                member.getBoundPoint(),
                member.getPointBalance()
        );
    }

    // 내 보유 아이템 조회
    public List<MemberItemResponse> getMyItems(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        return memberItemRepository.findByMemberWithItem(member)
                .stream()
                .map(MemberItemResponse::from)
                .collect(Collectors.toList());
    }

    // 아이템 장착
    @Transactional
    public MemberItemResponse equipItem(String username, Long memberItemId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        MemberItem memberItem = memberItemRepository.findById(memberItemId)
                .orElseThrow(() -> new ItemNotFoundException(memberItemId));

        // 본인 아이템인지 확인
        if (!memberItem.getMember().getId().equals(member.getId())) {
            throw new ItemNotFoundException(memberItemId);
        }

        // 같은 타입의 기존 장착 아이템 해제
        ItemType itemType = memberItem.getItem().getItemType();
        Optional<MemberItem> equippedItem = memberItemRepository.findEquippedItemByMemberAndType(member, itemType);
        equippedItem.ifPresent(MemberItem::unequip);

        // 새 아이템 장착
        memberItem.equip();
        memberItemRepository.save(memberItem);

        log.info("아이템 장착: 유저={}, 아이템={}", username, memberItem.getItem().getName());

        return MemberItemResponse.from(memberItem);
    }

    // 아이템 해제
    @Transactional
    public MemberItemResponse unequipItem(String username, Long memberItemId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        MemberItem memberItem = memberItemRepository.findById(memberItemId)
                .orElseThrow(() -> new ItemNotFoundException(memberItemId));

        // 본인 아이템인지 확인
        if (!memberItem.getMember().getId().equals(member.getId())) {
            throw new ItemNotFoundException(memberItemId);
        }

        memberItem.unequip();
        memberItemRepository.save(memberItem);

        log.info("아이템 해제: 유저={}, 아이템={}", username, memberItem.getItem().getName());

        return MemberItemResponse.from(memberItem);
    }

    // 장착 중인 아이템 조회
    public List<MemberItemResponse> getEquippedItems(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        return memberItemRepository.findEquippedItemsByMember(member)
                .stream()
                .map(MemberItemResponse::from)
                .collect(Collectors.toList());
    }
}