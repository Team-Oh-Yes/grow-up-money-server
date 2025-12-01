package com.ohyes.GrowUpMoney.domain.shop.controller;

import com.ohyes.GrowUpMoney.domain.shop.dto.request.PointExchangeRequest;
import com.ohyes.GrowUpMoney.domain.shop.dto.response.*;
import com.ohyes.GrowUpMoney.domain.shop.entity.enums.ItemType;
import com.ohyes.GrowUpMoney.domain.shop.service.ShopService;
import com.ohyes.GrowUpMoney.domain.auth.entity.CustomUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@Slf4j
public class ShopController {

    private final ShopService shopService;

    // 아이템 목록 조회 (공개 API)
    // GET /shop/items
    @GetMapping("/items")
    public ResponseEntity<List<ShopItemResponse>> getItems(
            @RequestParam(required = false) ItemType type) {

        List<ShopItemResponse> items;
        if (type != null) {
            items = shopService.getItemsByType(type);
        } else {
            items = shopService.getItems();
        }

        return ResponseEntity.ok(items);
    }

    // 아이템 구매
    // POST /shop/items/{itemId}/purchase
    @PostMapping("/items/{itemId}/purchase")
    public ResponseEntity<ItemPurchaseResponse> purchaseItem(
            @AuthenticationPrincipal CustomUser user,
            @PathVariable Long itemId) {

        log.info("아이템 구매 요청: 유저={}, 아이템ID={}", user.getUsername(), itemId);
        ItemPurchaseResponse response = shopService.purchaseItem(user.getUsername(), itemId);
        return ResponseEntity.ok(response);
    }

    // 재화 전환 (귀속 포인트 → 거래 가능 포인트)
    // POST /shop/points/exchange
    @PostMapping("/points/exchange")
    public ResponseEntity<PointExchangeResponse> exchangePoints(
            @AuthenticationPrincipal CustomUser user,
            @Valid @RequestBody PointExchangeRequest request) {

        log.info("포인트 전환 요청: 유저={}, 전환량={}", user.getUsername(), request.getAmount());
        PointExchangeResponse response = shopService.exchangePoints(user.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    // 내 보유 아이템 조회
    // GET /shop/my/items
    @GetMapping("/my/items")
    public ResponseEntity<List<MemberItemResponse>> getMyItems(
            @AuthenticationPrincipal CustomUser user) {

        List<MemberItemResponse> items = shopService.getMyItems(user.getUsername());
        return ResponseEntity.ok(items);
    }

    // 아이템 장착
    // POST /shop/my/items/{memberItemId}/equip
    @PostMapping("/my/items/{memberItemId}/equip")
    public ResponseEntity<MemberItemResponse> equipItem(
            @AuthenticationPrincipal CustomUser user,
            @PathVariable Long memberItemId) {

        log.info("아이템 장착 요청: 유저={}, 아이템ID={}", user.getUsername(), memberItemId);
        MemberItemResponse response = shopService.equipItem(user.getUsername(), memberItemId);
        return ResponseEntity.ok(response);
    }

    // 아이템 해제
    // POST /shop/my/items/{memberItemId}/unequip
    @PostMapping("/my/items/{memberItemId}/unequip")
    public ResponseEntity<MemberItemResponse> unequipItem(
            @AuthenticationPrincipal CustomUser user,
            @PathVariable Long memberItemId) {

        log.info("아이템 해제 요청: 유저={}, 아이템ID={}", user.getUsername(), memberItemId);
        MemberItemResponse response = shopService.unequipItem(user.getUsername(), memberItemId);
        return ResponseEntity.ok(response);
    }

    // 장착 중인 아이템 조회
    // GET /shop/my/items/equipped
    @GetMapping("/my/items/equipped")
    public ResponseEntity<List<MemberItemResponse>> getEquippedItems(
            @AuthenticationPrincipal CustomUser user) {

        List<MemberItemResponse> items = shopService.getEquippedItems(user.getUsername());
        return ResponseEntity.ok(items);
    }
}