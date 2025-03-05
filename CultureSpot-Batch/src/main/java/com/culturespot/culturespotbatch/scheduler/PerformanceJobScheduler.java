package com.culturespot.culturespotbatch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronTrigger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PerformanceJobScheduler {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private final JobLauncher jobLauncher;

	@Autowired
	private Job performanceJob;

	@Setter
	static class PerformanceQuartzJob extends QuartzJobBean {

		private JobLauncher jobLauncher;
		private Job job;

		@Override
		protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
			try {
				LocalDate now = LocalDate.now();
				String executeDate = now.format(formatter);

				JobParameters jobParameters = new JobParametersBuilder()
						.addString("executeDate", executeDate)
						.addLong("timestamp", System.currentTimeMillis())
						.toJobParameters();

				jobLauncher.run(job, jobParameters);
			} catch (Exception e) {
				throw new JobExecutionException(e);
			}
		}
	}

	@Bean
	public JobDetailFactoryBean performanceJobDetail() {
		JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
		jobDetailFactoryBean.setJobClass(PerformanceQuartzJob.class);
		jobDetailFactoryBean.setDurability(true);
		jobDetailFactoryBean.setName("Performance_Job");

		Map<String, Object> jobDataMap = new HashMap<>();
		jobDataMap.put("jobLauncher", jobLauncher);
		jobDataMap.put("job", performanceJob);
		jobDetailFactoryBean.setJobDataMap(new org.quartz.JobDataMap(jobDataMap));
		return jobDetailFactoryBean;
	}

	@Bean
	public CronTriggerFactoryBean performanceJobTrigger() {
		CronTriggerFactoryBean triggerFactoryBean = new CronTriggerFactoryBean();
		triggerFactoryBean.setJobDetail(performanceJobDetail().getObject());
		triggerFactoryBean.setName("Performance_Job_Trigger");

		triggerFactoryBean.setCronExpression("0/50 * * * * ?");
		triggerFactoryBean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
		return triggerFactoryBean;
	}

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setTriggers(performanceJobTrigger().getObject());
		schedulerFactoryBean.setAutoStartup(true);
		return schedulerFactoryBean;
	}
}