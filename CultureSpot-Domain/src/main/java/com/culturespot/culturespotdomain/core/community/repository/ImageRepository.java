package com.culturespot.culturespotdomain.core.community.repository;

import com.culturespot.culturespotdomain.core.community.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
