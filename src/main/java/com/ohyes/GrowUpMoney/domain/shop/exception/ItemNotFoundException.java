package com.ohyes.GrowUpMoney.domain.shop.exception;

import org.springframework.http.HttpStatus;

public class ItemNotFoundException extends ShopException {

    public ItemNotFoundException() {
        super(HttpStatus.NOT_FOUND, "아이템을 찾을 수 없습니다.");
    }

    public ItemNotFoundException(Long itemId) {
        super(HttpStatus.NOT_FOUND, String.format("아이템을 찾을 수 없습니다. (ID: %d)", itemId));
    }
}