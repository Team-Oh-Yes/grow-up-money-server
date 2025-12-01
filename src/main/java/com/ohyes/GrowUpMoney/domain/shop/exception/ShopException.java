package com.ohyes.GrowUpMoney.domain.shop.exception;

import com.ohyes.GrowUpMoney.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ShopException extends BaseException {

    public ShopException(HttpStatus status, String message) {
        super(status, message);
    }
}