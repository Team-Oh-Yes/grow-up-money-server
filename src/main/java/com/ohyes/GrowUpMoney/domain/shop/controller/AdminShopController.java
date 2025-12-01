package com.ohyes.GrowUpMoney.domain.shop.controller;

import com.ohyes.GrowUpMoney.domain.shop.dto.request.AdminShopItemRequest;
import com.ohyes.GrowUpMoney.domain.shop.dto.response.ShopItemResponse;
import com.ohyes.GrowUpMoney.domain.shop.service.AdminShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/shop")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminShopController {

    private final AdminShopService adminShopService;

    // 전체 아이템 목록 조회 (비활성 포함)
    // GET /admin/shop/items
    @GetMapping("/items")
    public ResponseEntity<List<ShopItemResponse>> getAllItems() {
        log.info("Admin - 전체 아이템 목록 조회");
        List<ShopItemResponse> items = adminShopService.getAllItems();
        return ResponseEntity.ok(items);
    }

    // 아이템 상세 조회
    // GET /admin/shop/items/{itemId}
    @GetMapping("/items/{itemId}")
    public ResponseEntity<ShopItemResponse> getItem(@PathVariable Long itemId) {
        log.info("Admin - 아이템 상세 조회: ID={}", itemId);
        ShopItemResponse item = adminShopService.getItem(itemId);
        return ResponseEntity.ok(item);
    }

    // 아이템 등록
    // POST /admin/shop/items
    @PostMapping("/items")
    public ResponseEntity<ShopItemResponse> createItem(
            @Valid @RequestBody AdminShopItemRequest request) {
        log.info("Admin - 아이템 등록: 이름={}", request.getName());
        ShopItemResponse item = adminShopService.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    // 아이템 수정
    // PUT /admin/shop/items/{itemId}
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ShopItemResponse> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody AdminShopItemRequest request) {
        log.info("Admin - 아이템 수정: ID={}", itemId);
        ShopItemResponse item = adminShopService.updateItem(itemId, request);
        return ResponseEntity.ok(item);
    }

    // 아이템 삭제
    // DELETE /admin/shop/items/{itemId}
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Map<String, Object>> deleteItem(@PathVariable Long itemId) {
        log.info("Admin - 아이템 삭제: ID={}", itemId);
        adminShopService.deleteItem(itemId);
        return ResponseEntity.ok(Map.of(
                "message", "아이템이 삭제되었습니다.",
                "success", true
        ));
    }

    // 아이템 활성화
    // PATCH /admin/shop/items/{itemId}/activate
    @PatchMapping("/items/{itemId}/activate")
    public ResponseEntity<ShopItemResponse> activateItem(@PathVariable Long itemId) {
        log.info("Admin - 아이템 활성화: ID={}", itemId);
        ShopItemResponse item = adminShopService.activateItem(itemId);
        return ResponseEntity.ok(item);
    }

    // 아이템 비활성화
    // PATCH /admin/shop/items/{itemId}/deactivate
    @PatchMapping("/items/{itemId}/deactivate")
    public ResponseEntity<ShopItemResponse> deactivateItem(@PathVariable Long itemId) {
        log.info("Admin - 아이템 비활성화: ID={}", itemId);
        ShopItemResponse item = adminShopService.deactivateItem(itemId);
        return ResponseEntity.ok(item);
    }
}