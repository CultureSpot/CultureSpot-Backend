package com.culturespot.culturespotapi.api;

import com.culturespot.culturespotapi.config.OpenApiProperties;
import com.culturespot.culturespotdomain.performance.dto.PerformanceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class BatchApiServiceImpl implements BatchApiService {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

	private final WebClient webClient;
	private final XmlMapper xmlMapper;
	private final OpenApiProperties properties;

	public BatchApiServiceImpl(WebClient.Builder webClientBuilder, OpenApiProperties properties) {
		this.webClient = webClientBuilder.build();
		this.xmlMapper = new XmlMapper();
		this.xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.properties = properties;
	}

	@Override
	public PerformanceResponse performanceOpenApiCall(int pageNo, int numOfRows) throws URISyntaxException, JsonProcessingException {

		LocalDate start = LocalDate.now();
		LocalDate end = start.plusDays(7);

		String startDate = start.format(DATE_FORMATTER);
		String endDate = end.format(DATE_FORMATTER);

		String encodedServiceKey = URLEncoder.encode(properties.getKey(), StandardCharsets.UTF_8);
		String url = properties.getApiCall() + "serviceKey=" +
				encodedServiceKey +
				"&PageNo=" + pageNo +
				"&numOfrows=" + numOfRows +
				"&from=" + startDate +
				"&to=" + endDate;
		System.out.println(url);

		URI uri = new URI(url);

		String xmlResponse = webClient.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(String.class)
				.timeout(Duration.ofSeconds(60))
				.block();
		return xmlMapper.readValue(xmlResponse, PerformanceResponse.class);
	}
}