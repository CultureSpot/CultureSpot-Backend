package com.culturespot.culturespotdomain.core.community.repository;

import com.culturespot.culturespotdomain.core.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
}
