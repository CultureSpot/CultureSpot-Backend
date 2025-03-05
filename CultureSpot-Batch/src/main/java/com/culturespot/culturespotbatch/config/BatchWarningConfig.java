package com.culturespot.culturespotbatch.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchWarningConfig {

	// https://github.com/spring-projects/spring-batch/issues/4245
	// https://github.com/spring-projects/spring-batch/issues/4519
	@Bean
	public static BeanDefinitionRegistryPostProcessor jobRegistryPostProcessorRemover() {
		return registry -> registry.removeBeanDefinition("jobRegistryBeanPostProcessor");
	}
}
