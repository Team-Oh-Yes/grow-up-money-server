package com.ohyes.GrowUpMoney.domain.shop.exception;

import org.springframework.http.HttpStatus;

public class InsufficientBoundPointException extends ShopException {

    public InsufficientBoundPointException() {
        super(HttpStatus.BAD_REQUEST, "귀속 포인트가 부족합니다.");
    }

    public InsufficientBoundPointException(Integer required, Integer current) {
        super(HttpStatus.BAD_REQUEST, String.format("귀속 포인트가 부족합니다. (필요: %d, 보유: %d)", required, current));
    }
}