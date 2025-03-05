package com.culturespot.culturespotapi.config;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

	@Value("${open-api.base-url}")
	private String baseUrl;

	@Bean
	public WebClient.Builder webClientBuilder() {
		return WebClient.builder()
				.baseUrl(baseUrl)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
				.codecs(configurer -> {
					configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024);
				})
				.clientConnector(new ReactorClientHttpConnector(
						HttpClient.create()
								.responseTimeout(Duration.ofSeconds(60))
								.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000)
								.option(ChannelOption.SO_KEEPALIVE, true)
								.compress(true)
				));
	}
}