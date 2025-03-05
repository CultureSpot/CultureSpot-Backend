package com.culturespot.culturespotbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = {
		"com.culturespot.culturespotbatch",
		"com.culturespot.culturespotapi",
		"com.culturespot.culturespotdomain",
		"com.culturespot.culturespotcommon"
})
public class CultureSpotBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(CultureSpotBatchApplication.class, args);
	}
}
