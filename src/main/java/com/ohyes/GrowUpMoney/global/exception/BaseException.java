package com.ohyes.GrowUpMoney.global.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


public abstract class BaseException extends RuntimeException{
    private final HttpStatus status;

    public BaseException(HttpStatus status, String message){
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus(){
        return status;
    }

}
