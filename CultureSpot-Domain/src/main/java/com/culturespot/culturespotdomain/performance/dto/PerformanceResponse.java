package com.culturespot.culturespotdomain.performance.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@JacksonXmlRootElement(localName = "response")
public class PerformanceResponse {

	@JacksonXmlProperty(localName = "header")
	private Header header;

	@JacksonXmlProperty(localName = "body")
	private Body body;

	@Data
	public static class Header {

		@JacksonXmlProperty(localName = "reseultCode") 	// XML에는 "reseultCode"로 오타가 있음에 주의
		private String resultCode;

		@JacksonXmlProperty(localName = "resultMsg")
		private String resultMsg;
	}

	@Data
	public static class Body {
		@JacksonXmlProperty(localName = "totalCount")
		private int totalCount;

		@JacksonXmlProperty(localName = "PageNo")
		private int pageNo;

		@JacksonXmlProperty(localName = "numOfrows")
		private int numOfRows;

		@JacksonXmlProperty(localName = "items")
		private Items items;
	}

	@Data
	public static class Items {
		@JacksonXmlElementWrapper(useWrapping = false)
		@JacksonXmlProperty(localName = "item")
		private List<Item> itemList;
	}

	@Data
	public static class Item {
		@JacksonXmlProperty(localName = "serviceName")
		private String serviceName;

		@JacksonXmlProperty(localName = "seq")
		private String seq;

		@JacksonXmlProperty(localName = "title")
		private String title;

		@JacksonXmlProperty(localName = "startDate")
		private String startDate;

		@JacksonXmlProperty(localName = "endDate")
		private String endDate;

		@JacksonXmlProperty(localName = "place")
		private String place;

		@JacksonXmlProperty(localName = "realmName")
		private String realmName;

		@JacksonXmlProperty(localName = "area")
		private String area;

		@JacksonXmlProperty(localName = "thumbnail")
		private String thumbnail;

		@JacksonXmlProperty(localName = "gpsX")
		private String gpsX;

		@JacksonXmlProperty(localName = "gpsY")
		private String gpsY;
	}
}