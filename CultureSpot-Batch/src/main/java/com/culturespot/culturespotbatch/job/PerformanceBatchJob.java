package com.culturespot.culturespotbatch.job;

import com.culturespot.culturespotapi.api.BatchApiService;
import com.culturespot.culturespotbatch.alarm.BatchResultToDiscord;
import com.culturespot.culturespotbatch.processor.PerformanceItemProcessor;
import com.culturespot.culturespotbatch.reader.PerformanceItemReader;
import com.culturespot.culturespotbatch.scheduler.CreateDateJobParameter;
import com.culturespot.culturespotbatch.writer.PerformanceItemWriter;
import com.culturespot.culturespotdomain.performance.dto.PerformanceDto;
import com.culturespot.culturespotdomain.performance.entity.Performance;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PerformanceBatchJob {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final String JOB_NAME = "한눈에보는문화정보조회서비스_분야별공연문화정보목록조회";
	private static final String BEAN_PREFIX = JOB_NAME + "_";

	private final PlatformTransactionManager platformTransactionManager;
	private final EntityManagerFactory emf;
	private final BatchApiService batchApiService;
	private final JobRepository jobRepository;
	private final BatchResultToDiscord batchResultToDiscord;

	private int chunkSize;
	private String jobName;

	@Value("${chunk-size:1000}")
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	@Bean(BEAN_PREFIX + "jobParameter")
	@JobScope
	public CreateDateJobParameter createDateJobParameter() {
		return new CreateDateJobParameter();
	}

	@Bean
	public Job performanceJob() {
		return new JobBuilder(JOB_NAME, jobRepository)
				.listener(batchResultToDiscord)	// 디스코드 알림 전송
				.start(performanceStep())
				.build();
	}

	@Bean(BEAN_PREFIX + "_step")
	@JobScope
	public Step performanceStep() {
		LocalDate executeDate = createDateJobParameter().getExecuteDate();
		String stepName = JOB_NAME + "_" + executeDate.format(DATE_FORMATTER) + "_step";

		return new StepBuilder(stepName, jobRepository)
				.<PerformanceDto, Performance> chunk(chunkSize, platformTransactionManager)
				.reader(performanceItemReader())
				.processor(performanceItemProcessor())
				.writer(performanceItemWriter())
				.build();
	}

	@Bean(BEAN_PREFIX + "_reader")
	@StepScope
	public PerformanceItemReader performanceItemReader() {
		return new PerformanceItemReader(batchApiService);
	}

	@Bean(BEAN_PREFIX + "_processor")
	@StepScope
	public PerformanceItemProcessor performanceItemProcessor() {
		return new PerformanceItemProcessor();
	}

	@Bean(BEAN_PREFIX + "_writer")
	@StepScope
	public PerformanceItemWriter performanceItemWriter() {
		return new PerformanceItemWriter(emf);
	}
}