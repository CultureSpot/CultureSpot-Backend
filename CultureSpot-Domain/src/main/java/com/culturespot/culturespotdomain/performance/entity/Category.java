package com.culturespot.culturespotdomain.performance.entity;

import lombok.Getter;

@Getter
public enum Category {

	EXHIBITION("전시"),
	THEATER("연극"),
	MUSIC("음악"),
	DANCING("무용"),
	ART("미술"),
	BUILDING("건축"),
	VIDEO("영상"),
	LITERATURE("문학"),
	CULTURAL("문화정책"),
	FIESTA("축제"),
	ETC("기타");

	private final String description;

	Category(String description) {
		this.description = description;
	}
}
