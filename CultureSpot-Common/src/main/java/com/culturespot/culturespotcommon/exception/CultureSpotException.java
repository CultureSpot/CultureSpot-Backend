package com.culturespot.culturespotcommon.exception;

import com.culturespot.culturespotcommon.error.ErrorCode;
import lombok.Getter;

// 비즈니스 최상위 예외 계층
@Getter
public class CultureSpotException extends RuntimeException {

	private final ErrorCode errorCode;

	public CultureSpotException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public CultureSpotException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
}
