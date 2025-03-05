package com.culturespot.culturespotbatch.writer;

import com.culturespot.culturespotdomain.performance.entity.Performance;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;

@Slf4j
public class PerformanceItemWriter implements ItemWriter<Performance> {

	private final EntityManagerFactory emf;
	private final JpaItemWriter<Performance> writer;

	public PerformanceItemWriter(EntityManagerFactory emf) {
		this.emf = emf;
		this.writer = new JpaItemWriterBuilder<Performance>()
				.entityManagerFactory(emf)
				.build();
	}

	@Override
	public void write(Chunk<? extends Performance> chunk) throws Exception {
		writer.write(chunk);
	}
}
