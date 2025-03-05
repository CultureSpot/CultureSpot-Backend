package com.culturespot.culturespotapi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "open-api")
public class OpenApiProperties {
	private String key;
	private String apiCall;
}
