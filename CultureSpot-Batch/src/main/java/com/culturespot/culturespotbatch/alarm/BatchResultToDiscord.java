package com.culturespot.culturespotbatch.alarm;

import com.culturespot.culturespotcommon.exception.DiscordException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class BatchResultToDiscord implements JobExecutionListener {

	@Value("${discord.webhook}")
	private String webHook;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		sendDiscordMessage(
				"배치 작업 시작",
				"배치 작업 전",
				0x3498DB // 파란색
		);
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		boolean isSuccess = jobExecution.getStatus().name().equals("COMPLETED");

		sendDiscordMessage(
				isSuccess ? "배치 작업 성공" : "배치 작업 실패",
				"배치 작업 후",
				isSuccess ? 0x57F267 : 0xED4245 // 성공시 녹색, 실패시 빨간색
		);
	}

	private void sendDiscordMessage(String title, String description, int color) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			DiscordMessage message = new DiscordMessage(title, description, color);

			HttpEntity<DiscordMessage> request = new HttpEntity<>(message, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.postForEntity(webHook, request, String.class);

			if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
				log.error("메시지 전송 중 오류 발생: {}", response.getStatusCode());
			}
		} catch (Exception e) {
			log.error("Discord 메시지 전송 실패", e);
		}
	}
}