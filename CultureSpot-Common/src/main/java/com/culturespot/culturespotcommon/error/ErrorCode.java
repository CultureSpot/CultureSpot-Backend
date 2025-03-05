package com.culturespot.culturespotcommon.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

	// Common
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST.value(),"C001", "유효하지 않은 입력값 형식"),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(), "C002", "허용되지 않는 메서드"),
	HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "C005", "접근 권한 불충분");

	// User

	// Batch

	// Community

	private final int status;
	private final String code;
	private final String message;

	ErrorCode(final int status, final String code, final String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}
