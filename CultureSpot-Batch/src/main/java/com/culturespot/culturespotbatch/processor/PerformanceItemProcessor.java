package com.culturespot.culturespotbatch.processor;

import com.culturespot.culturespotdomain.performance.dto.PerformanceDto;
import com.culturespot.culturespotdomain.performance.entity.Category;
import com.culturespot.culturespotdomain.performance.entity.Performance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class PerformanceItemProcessor implements ItemProcessor<PerformanceDto, Performance> {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

	@Override
	public Performance process(PerformanceDto item) throws Exception {
		if (item == null) {
			return null;
		}

		// Map realm names to Category enum
		Category category = mapRealmNameToCategory(item.getRealmName());

		// Parse GPS coordinates, defaulting to 0.0 if null or invalid
		double gpsX = parseCoordinate(item.getGpsX());
		double gpsY = parseCoordinate(item.getGpsY());

		// Parse sequence number, defaulting to 0 if null or invalid
		int seq = parseSequence(item.getSeq());

		// Parse dates with default handling
		LocalDate startDate = parseDate(item.getStartDate());
		LocalDate endDate = parseDate(item.getEndDate());

		return Performance.builder()
				.serviceName(item.getServiceName())
				.seq(seq)
				.title(item.getTitle())
				.startDate(startDate)
				.endDate(endDate)
				.place(item.getPlace())
				.category(category)
				.area(item.getArea())
				.thumbnail(item.getThumbnail())
				.gpsX(gpsX)
				.gpsY(gpsY)
				.build();
	}

	private Category mapRealmNameToCategory(String realmName) {
		switch (realmName) {
			case "전시":
				return Category.EXHIBITION;
			case "연극":
				return Category.THEATER;
			case "음악":
				return Category.MUSIC;
			case "무용":
				return Category.DANCING;
			case "미술":
				return Category.ART;
			case "건축":
				return Category.BUILDING;
			case "영상":
				return Category.VIDEO;
			case "문학":
				return Category.LITERATURE;
			case "문화정책":
				return Category.CULTURAL;
			case "축제":
				return Category.FIESTA;
			default:
				return Category.ETC;
		}
	}

	private double parseCoordinate(String coordinate) {
		if (!StringUtils.hasText(coordinate)) {
			return 0.0;
		}
		try {
			return Double.parseDouble(coordinate);
		} catch (NumberFormatException e) {
			//log.warn("Invalid coordinate value: {}, defaulting to 0.0", coordinate);
			return 0.0;
		}
	}

	private int parseSequence(String seqStr) {
		if (!StringUtils.hasText(seqStr)) {
			//log.warn("Empty sequence, defaulting to 0");
			return 0;
		}
		try {
			return Integer.parseInt(seqStr);
		} catch (NumberFormatException e) {
			//log.warn("Invalid sequence value: {}, defaulting to 0", seqStr);
			return 0;
		}
	}

	private LocalDate parseDate(String dateStr) {
		if (!StringUtils.hasText(dateStr)) {
			//log.warn("Empty date, defaulting to current date");
			return LocalDate.now();
		}
		try {
			return LocalDate.parse(dateStr, DATE_FORMATTER);
		} catch (Exception e) {
			//log.warn("Invalid date value: {}, defaulting to current date", dateStr);
			return LocalDate.now();
		}
	}
}