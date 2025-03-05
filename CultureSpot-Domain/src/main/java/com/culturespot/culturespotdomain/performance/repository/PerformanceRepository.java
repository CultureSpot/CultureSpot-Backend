package com.culturespot.culturespotdomain.performance.repository;

import com.culturespot.culturespotdomain.performance.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
}
