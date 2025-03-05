package com.culturespot.culturespotbatch.job;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
@NoArgsConstructor
public class QuerydslPagingItemReaderJobParameter {

	private LocalDate txDate;

	@Value("#{jobParameters[txDate]}")
	public void setTxDate(String txDate) {
		this.txDate = LocalDate.parse(txDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
}
