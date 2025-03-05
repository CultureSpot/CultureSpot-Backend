package com.culturespot.culturespotdomain.performance.entity;

import com.culturespot.culturespotdomain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performance extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "performance_id")
	private Long id;

	@Column(name = "service_name")
	private String serviceName;	// 서비스명

	@Column(name = "seq")
	private int seq;			// 일련번호

	@Column(name = "title")
	private String title;		// 제목

	@Column(name = "startDate")
	private LocalDate startDate;	// 시작일자

	@Column(name = "endDate")
	private LocalDate endDate;		// 마감일자

	@Column(name = "place")
	private String place;		// 장소

	@Enumerated(EnumType.STRING)
	@Column(name = "category")
	private Category category;	// 분류명

	@Column(name = "area")
	private String area;		// 지역

	@Column(name = "thumbnail")
	private String thumbnail;	// 썸네일

	@Column(name = "gpsX")
	private double gpsX;		// GPS-X좌표

	@Column(name = "gpsY")
	private double gpsY;		// GPS=Y좌표

	@Builder
	private Performance(String serviceName, int seq, String title, LocalDate startDate, LocalDate endDate,
						String place, Category category, String area, String thumbnail, double gpsX, double gpsY) {
		this.serviceName = serviceName;
		this.seq = seq;
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.place = place;
		this.category = category;
		this.area = area;
		this.thumbnail = thumbnail;
		this.gpsX = gpsX;
		this.gpsY = gpsY;
	}
}
