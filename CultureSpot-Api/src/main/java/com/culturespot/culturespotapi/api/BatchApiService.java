package com.culturespot.culturespotapi.api;

import com.culturespot.culturespotdomain.performance.dto.PerformanceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

public interface BatchApiService {

	PerformanceResponse performanceOpenApiCall(int pageNo, int numOfRows) throws UnsupportedEncodingException, URISyntaxException, JsonProcessingException;
}
