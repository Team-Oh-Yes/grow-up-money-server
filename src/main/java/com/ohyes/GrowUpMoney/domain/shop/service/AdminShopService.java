package com.ohyes.GrowUpMoney.domain.shop.service;

import com.ohyes.GrowUpMoney.domain.shop.dto.request.AdminShopItemRequest;
import com.ohyes.GrowUpMoney.domain.shop.dto.response.ShopItemResponse;
import com.ohyes.GrowUpMoney.domain.shop.entity.ShopItem;
import com.ohyes.GrowUpMoney.domain.shop.entity.enums.ItemType;
import com.ohyes.GrowUpMoney.domain.shop.exception.ItemNotFoundException;
import com.ohyes.GrowUpMoney.domain.shop.repository.ShopItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminShopService {

    private final ShopItemRepository shopItemRepository;

    // 전체 아이템 목록 조회 (비활성 포함)
    public List<ShopItemResponse> getAllItems() {
        return shopItemRepository.findAll()
                .stream()
                .map(ShopItemResponse::from)
                .collect(Collectors.toList());
    }

    // 아이템 상세 조회
    public ShopItemResponse getItem(Long itemId) {
        ShopItem item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        return ShopItemResponse.from(item);
    }

    // 아이템 등록
    @Transactional
    public ShopItemResponse createItem(AdminShopItemRequest request) {
        ShopItem item = new ShopItem();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setItemType(request.getItemType());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());
        item.setItemValue(request.getItemValue());
        item.setIsActive(true);

        shopItemRepository.save(item);

        log.info("아이템 등록: ID={}, 이름={}", item.getId(), item.getName());

        return ShopItemResponse.from(item);
    }

    // 아이템 수정
    @Transactional
    public ShopItemResponse updateItem(Long itemId, AdminShopItemRequest request) {
        ShopItem item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setItemType(request.getItemType());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());
        item.setItemValue(request.getItemValue());

        shopItemRepository.save(item);

        log.info("아이템 수정: ID={}, 이름={}", item.getId(), item.getName());

        return ShopItemResponse.from(item);
    }

    // 아이템 삭제 (실제 삭제)
    @Transactional
    public void deleteItem(Long itemId) {
        ShopItem item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        shopItemRepository.delete(item);

        log.info("아이템 삭제: ID={}, 이름={}", itemId, item.getName());
    }

    // 아이템 활성/비활성 변경
    @Transactional
    public ShopItemResponse toggleItemStatus(Long itemId) {
        ShopItem item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        item.setIsActive(!item.getIsActive());
        shopItemRepository.save(item);

        log.info("아이템 상태 변경: ID={}, 활성={}", item.getId(), item.getIsActive());

        return ShopItemResponse.from(item);
    }

    // 아이템 활성화
    @Transactional
    public ShopItemResponse activateItem(Long itemId) {
        ShopItem item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        item.setIsActive(true);
        shopItemRepository.save(item);

        log.info("아이템 활성화: ID={}", item.getId());

        return ShopItemResponse.from(item);
    }

    // 아이템 비활성화
    @Transactional
    public ShopItemResponse deactivateItem(Long itemId) {
        ShopItem item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        item.setIsActive(false);
        shopItemRepository.save(item);

        log.info("아이템 비활성화: ID={}", item.getId());

        return ShopItemResponse.from(item);
    }
}