package com.culturespot.culturespotcommon.global.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    private final int code;
    private final String message;

    public DomainException(DomainExceptionCode exceptionCode) {
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMessage();
    }
}
