package com.culturespot.culturespotcommon.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

	private String message;
	private int status;
	private List<FieldError> errors;
	private String code;

	public ErrorResponse(String message, int status, List<FieldError> errors, String code) {
		this.message = message;
		this.status = status;
		this.errors = errors;
		this.code = code;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	static class FieldError {
		private String field;
		private String value;
		private String reason;
	}

	// @Valid 어노테이션 검증 추가 시 정적 팩토리 메서드 추가 예정
}
