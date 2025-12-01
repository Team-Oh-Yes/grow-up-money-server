package com.ohyes.GrowUpMoney.domain.shop.exception;

import org.springframework.http.HttpStatus;

public class AlreadyOwnedItemException extends ShopException {

    public AlreadyOwnedItemException() {
        super(HttpStatus.CONFLICT, "이미 보유한 아이템입니다.");
    }
}