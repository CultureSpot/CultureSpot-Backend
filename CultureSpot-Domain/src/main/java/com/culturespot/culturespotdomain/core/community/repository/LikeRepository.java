package com.culturespot.culturespotdomain.core.community.repository;

import com.culturespot.culturespotdomain.core.community.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
}
