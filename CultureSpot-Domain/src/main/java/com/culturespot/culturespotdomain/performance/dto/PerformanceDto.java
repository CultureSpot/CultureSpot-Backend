package com.culturespot.culturespotdomain.performance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerformanceDto {

	private String serviceName;
	private String seq;
	private String title;
	private String startDate;
	private String endDate;
	private String place;
	private String realmName;
	private String area;
	private String thumbnail;
	private String gpsX;
	private String gpsY;
}
