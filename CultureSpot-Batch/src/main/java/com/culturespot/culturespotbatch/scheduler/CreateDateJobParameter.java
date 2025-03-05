package com.culturespot.culturespotbatch.scheduler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
public class CreateDateJobParameter {

	private final LocalDate executeDate;

	public CreateDateJobParameter() {
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		this.executeDate = LocalDate.parse(today, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
}
